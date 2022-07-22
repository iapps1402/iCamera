package ir.stackcode.videoproject.application

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.hardware.usb.UsbManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.stackcode.videoproject.utils.OnUSBListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.IOException
import java.lang.reflect.Method
import kotlin.properties.Delegates


abstract class BaseActivity : AppCompatActivity(), SerialInputOutputManager.Listener,
    OnUSBListener {

    private var touchView: View? = null

    private var port: UsbSerialPort? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }


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

        //      (application as MyApplication).resetTimer()
    }

    fun setFocusView(view: View) {
        touchView = view
    }

    fun releaseFocusView() {
        touchView = null
    }

    fun canOpenDialog() = touchView == null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onUserInteraction() {
        //       if (userInteraction)
        (application as MyApplication).resetTimer()
    }

    override fun onRunError(e: Exception) {
        Log.d(MyApplication.TAG, "Error! " + e.message)
    }

    fun writeData(data: String) {
        port?.write(data.toByteArray(), 2000)
        Log.d(MyApplication.TAG, "Data sent: $data")
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

                if (!critical && lastPointReceived != pointReceived && pointReceived.length == 12) {
                    performSimulation()
                } else if(!critical) {
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
                Log.d(MyApplication.TAG, "Data received: " + String(data))
            }
        }
    }


    private var critical = false

    private fun performSimulation() {
        critical = true
        lastPointReceived = pointReceived
        Log.d("Performing Point ", pointReceived)
        val rPoint = pointReceived.replace("FFFF", "")
        val x = Integer.parseInt(rPoint.substring(2, 4) + rPoint.substring(0, 2), 16)
        val y = Integer.parseInt(rPoint.substring(6) + rPoint.substring(4, 6), 16)

        //simulate(x.toFloat(), y.toFloat())
        simulateClick(x.toFloat(), y.toFloat())

        pointReceived = ""

        runOnUiThread {
            object: CountDownTimer(400, 400){
                override fun onTick(p0: Long) {}
                override fun onFinish() {
                        lastPointReceived = ""
                }
            }.start()
        }

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

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    private fun simulateClick(fakeX: Float, fakeY: Float) {
        val x = ((screenWidth / 1024.0) * fakeX).toFloat()
        val y = ((screenHeight / 600.0) * fakeY).toFloat()

        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()
        val properties = arrayOfNulls<MotionEvent.PointerProperties>(1)
        val pp1 = MotionEvent.PointerProperties()
        pp1.id = 0
        pp1.toolType = MotionEvent.TOOL_TYPE_FINGER
        properties[0] = pp1
        val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(1)
        val pc1 = MotionEvent.PointerCoords()

        if (touchView == null) {
            pc1.x = x
            pc1.y = y
        } else {
            val point = IntArray(2)
            touchView!!.getLocationOnScreen(point)

            pc1.x = x - point[0]
            pc1.y = y - point[1]

//            Log.d("ssssssssssssss pc", x.toString() + " " + y)
//            Log.d("ssssssssssssss pc", point[0].toString() + " " + point[1])
//            Log.d("ssssssssssssss pc1", pc1.x.toString() + " " + pc1.y.toString())
        }


        pc1.pressure = 1f
        pc1.size = 1f
        pointerCoords[0] = pc1
        var motionEvent = MotionEvent.obtain(
            downTime, eventTime,
            MotionEvent.ACTION_DOWN, 1, properties,
            pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
        )

        try {
            if (touchView == null)
                dispatchTouchEvent(motionEvent)

            touchView?.let {
                it.post {
                    it.dispatchTouchEvent(motionEvent)
                }
            }

            motionEvent = MotionEvent.obtain(
                downTime, eventTime,
                MotionEvent.ACTION_UP, 1, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
            )

            if (touchView == null)
                dispatchTouchEvent(motionEvent)

            touchView?.let {
                it.post {
                    it.dispatchTouchEvent(motionEvent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        try {
            if (!hasFocus) {
                val service = getSystemService("statusbar")
                val statusbarManager = Class.forName("android.app.StatusBarManager")
                val collapse: Method = statusbarManager.getMethod("collapse")
                collapse.isAccessible = true
                collapse.invoke(service)
            }
        } catch (ex: Exception) {
        }
    }

    private var dataReceived = ""
    private var pointReceived = ""
    private var lastPointReceived = ""

//    fun setUserInteraction(userInteraction: Boolean) {
//        this.userInteraction = userInteraction
//        (application as MyApplication).cancelTimer()
//    }

//    private var userInteraction = true

}