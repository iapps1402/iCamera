package ir.stackcode.icamera.application

import android.app.Application
import android.app.Instrumentation
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.hardware.usb.UsbManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.yariksoffice.lingver.Lingver
import helper.HotKeys.RECEIVE_OPEN_FIRST_CAMERA
import helper.HotKeys.RECEIVE_OPEN_SECOND_CAMERA
import helper.LocaleManager
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

import ir.stackcode.icamera.utils.OnUSBListener
import ir.stackcode.icamera.view.BlackActivity

import ir.stackcode.icamera.R
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

class MyApplication : Application(), SerialInputOutputManager.Listener, OnUSBListener {
    private var port: UsbSerialPort? = null
    override fun onCreate() {
        super.onCreate()

        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels

        val language = LocaleManager.getRealLanguage(this)
        Locale.setDefault(Locale(language))

        Lingver.init(this, language)
        Lingver.getInstance().setLocale(this, language)
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath(if (language == "fa") "fonts/IRANSans_Light.ttf" else "fonts/IRANSans_Light_Eng_Numerals.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        timer = object : CountDownTimer(interactionTimeout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                blackenScreen()

            }
        }

        timer.start()


        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers =
            UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }

        val drivers =
            availableDrivers.filter {
                it.device.let { device -> device.productId == 60000 && device.vendorId == 4292 }
            }

        if (drivers.isEmpty())
            return

        val driver = drivers[0]
        val connection = manager.openDevice(driver.device)
        if (connection == null) {
            val mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent("1000"), 0)
            manager.requestPermission(driver.device, mPermissionIntent)
            return
        }
        try {
            port = driver.ports[0]
            port!!.open(connection)
            port!!.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val usbIoManager = SerialInputOutputManager(port, this)
            usbIoManager.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun blackenScreen() {
        startActivity(Intent(this@MyApplication, BlackActivity::class.java).apply {
            flags = FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
        })
        timer.cancel()
    }

    fun startTimer() {
        interactionTimeout = 120000
        timer.start()
    }

    fun cancelTimer() {
        timer.cancel()
    }

    fun resetTimer() {
        interactionTimeout = 120000
        timer.cancel()
        timer.start()
    }

    private lateinit var timer: CountDownTimer

    companion object {
        const val TAG = "CameraTech"
    }

    private var interactionTimeout: Long = 120000

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onNewData(data: ByteArray) {
        val result = StringBuilder()
        for (bb in data) {
            result.append(String.format("%02X", bb))
        }

        val point = result.toString()

        when {
            pointReceived.startsWith("FFFF") && point.length == 8
                    || pointReceived.startsWith("FFFF") && pointReceived.length == 6 && point.length == 6
                    || pointReceived.startsWith("FFFF") && pointReceived.length == 8 && point.length == 4
                    || pointReceived.startsWith("FFFF") && pointReceived.length == 10 && point.length == 2 -> {
                pointReceived += point

                Log.d("Point Received", pointReceived)

                if (!critical && pointReceived.length == 12) {
                    performSimulation()
                } else if (!critical) {
                    pointReceived = ""
                }

            }

            point == "FF" && pointReceived.isEmpty() -> {
                pointReceived = "FF"
            }

            point == "FF" && pointReceived == "FF" -> {
                pointReceived = "FFFF"
            }

            pointReceived.startsWith("FFFF") && pointReceived.length < 12 ->
                pointReceived = pointReceived.plus(point)

            pointReceived.startsWith("FFFF") && pointReceived.length > 12 -> {
                pointReceived = ""
            }

            else -> {
                dataReceived += String(data)
                startRingtone()
                if (dataReceived == RECEIVE_OPEN_FIRST_CAMERA || dataReceived == RECEIVE_OPEN_SECOND_CAMERA) {
                    sendBroadcast(Intent().apply {
                        putExtra("data_received", dataReceived)
                        action = "data_received"
                    })

                    dataReceived = ""
                }

                if (dataReceived.length > 2)
                    dataReceived = ""

                Log.d(TAG, "Data received: " + String(data))
            }
        }
    }

    private fun startRingtone() {
        val afd = resources.assets.openFd("mp3/ringtone.mp3")
        val player = MediaPlayer()
        try {

            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            player.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        player.start()
    }

    private var critical = false

    private fun performSimulation() {
        critical = true
        Log.d("Performing Point ", pointReceived)
        try {
            val rPoint = pointReceived.replace("FFFF", "")
            val x = Integer.parseInt(rPoint.substring(2, 4) + rPoint.substring(0, 2), 16)
            val y = Integer.parseInt(rPoint.substring(6) + rPoint.substring(4, 6), 16)

            simulate2(x.toFloat(), y.toFloat())
            //simulateClick(x.toFloat(), y.toFloat())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        pointReceived = ""
        critical = false
    }

    private fun simulate(fakeX: Float, fakeY: Float) {
        val x = ((resources.displayMetrics.widthPixels / 1024.0) * fakeX).toFloat()
        val y = ((resources.displayMetrics.heightPixels / 600.0) * fakeY).toFloat()
        val process = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(process.outputStream)
        val cmd = "/system/bin/input tap $x $y\n"
        os.writeBytes(cmd)
        os.writeBytes("exit\n")
        os.flush()
        os.close()
        process.waitFor()
    }

    private fun simulate2(fakeX: Float, fakeY: Float) {
        val x = ((resources.displayMetrics.widthPixels / 1024.0) * fakeX).toFloat()
        val y = ((resources.displayMetrics.heightPixels / 600.0) * fakeY).toFloat()
        object : Thread() {
            override fun run() {
                val m_Instrumentation = Instrumentation()
                m_Instrumentation.sendPointerSync(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN, x, y, 0
                    )
                )
                m_Instrumentation.sendPointerSync(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP, x, y, 0
                    )
                )
            }
        }.apply { start() }
    }

    override fun onRunError(e: Exception) {
        Log.d(TAG, "Error! " + e.message)
    }

    fun writeData(data: String) {
        try {
            port?.write(data.toByteArray(), 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG, "Data sent: $data")
    }


    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var dataReceived = ""
    private var pointReceived = ""
    override fun onUSBNewData(data: String) {

        Log.d("ssssss", data)
    }
}