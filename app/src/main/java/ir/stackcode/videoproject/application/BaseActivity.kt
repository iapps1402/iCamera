package ir.stackcode.videoproject.application

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.stackcode.videoproject.utils.OnUSBListener
import java.io.IOException
import java.lang.Exception

abstract class BaseActivity : AppCompatActivity(), SerialInputOutputManager.Listener,
    OnUSBListener {
    private var port: UsbSerialPort? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers =
            UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }

        val drivers =
            availableDrivers.filter { it.device.let { device -> device.productId == 60000 && device.vendorId == 4292 } }

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
        val usbIoManager = SerialInputOutputManager(port, this)
        usbIoManager.start()

        //      (application as MyApplication).resetTimer()
    }

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
        dataReceived += String(data)
        Log.d(MyApplication.TAG, "Data received: " + String(data))
    }

    private var dataReceived = ""

//    fun setUserInteraction(userInteraction: Boolean) {
//        this.userInteraction = userInteraction
//        (application as MyApplication).cancelTimer()
//    }

//    private var userInteraction = true

}