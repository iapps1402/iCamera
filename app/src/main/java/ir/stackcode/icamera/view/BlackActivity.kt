package ir.stackcode.icamera.view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
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
import helper.HotKeys
import helper.HotKeys.RECEIVE_OPEN_FIRST_CAMERA
import helper.HotKeys.RECEIVE_OPEN_SECOND_CAMERA
import ir.stackcode.icamera.application.MyApplication
import ir.stackcode.icamera.view.ui.theme.VideoProjectTheme


class BlackActivity : ComponentActivity() {
    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        (application as MyApplication).writeData(HotKeys.SEND_EXIT)

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

        val params = window.attributes
        params.screenBrightness = 0f
        window.attributes = params
    }

    private fun finishActivity() {
        val params = window.attributes
        params.screenBrightness = -1f
        window.attributes = params
        (application as MyApplication).startTimer()
        finishAffinity()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onBackPressed() {
        return
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, mIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    private val mIntentFilter = IntentFilter("data_received")
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            val position = when (intent.getStringExtra("data_received")) {
                RECEIVE_OPEN_FIRST_CAMERA -> 0
                RECEIVE_OPEN_SECOND_CAMERA -> 1
                else -> -1
            }

            if(position != -1) {
                startActivity(Intent(this@BlackActivity, MainActivity::class.java))
                startActivity(Intent(this@BlackActivity, CameraActivity::class.java).apply {
                    putExtra("position", position)
                })
                finish()
            }
        }
    }
}