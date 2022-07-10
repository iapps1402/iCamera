package ir.stackcode.videoproject.view

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import helper.HotKeys
import helper.HotKeys.RECEIVE_OPEN_FIRST_CAMERA
import helper.HotKeys.RECEIVE_TURN_ON_SCREEN
import ir.stackcode.videoproject.application.MyApplication
import ir.stackcode.videoproject.view.ui.theme.VideoProjectTheme
import java.io.IOException


class BlackActivity : ComponentActivity(), SerialInputOutputManager.Listener {
    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            VideoProjectTheme(darkTheme = true) {
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)
                        .clickable(interactionSource = interactionSource, indication = null) {
                            finishActivity()
                        }
                )

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }


        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers =
            UsbSerialProber.getDefaultProber().findAllDrivers(manager)

        if (availableDrivers.isNotEmpty()) {

            val driver = availableDrivers[0]
            val connection = manager.openDevice(driver.device)
            if (connection == null) {
                Toast.makeText(this, "empty", Toast.LENGTH_LONG).show()
                val mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent("1000"), 0)
                manager.requestPermission(driver.device, mPermissionIntent)
                return
            }
            val port =
                driver.ports[0]
            try {
                port.open(connection)
                port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val usbIoManager = SerialInputOutputManager(port, this)
            usbIoManager.start()

            port.write(HotKeys.SEND_EXIT.toByteArray(), 2000)
            Log.d(MyApplication.TAG, "Data sent: ${HotKeys.SEND_EXIT}")
        }

        val params = window.attributes
        params.screenBrightness = 0f
        window.attributes = params
    }

    private var dataReceived = ""
    override fun onNewData(data: ByteArray) {
        dataReceived += String(data)
        when {
            RECEIVE_TURN_ON_SCREEN in dataReceived -> {
                finishActivity()
                dataReceived = ""
            }

            RECEIVE_OPEN_FIRST_CAMERA in dataReceived -> {
                startActivity(Intent(this, CameraActivity::class.java).apply {
                    putExtra("position", 0)
                })

                dataReceived = ""
            }
        }
        Log.d(MyApplication.TAG, "Data received: " + String(data))


    }

    private fun finishActivity() {
        val params = window.attributes
        params.screenBrightness = -1f
        window.attributes = params
        (application as MyApplication).startTimer()
        finishAffinity()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onRunError(e: Exception?) {
        Log.d(MyApplication.TAG, "error reading")
    }

    override fun onBackPressed() {
        return
    }
}