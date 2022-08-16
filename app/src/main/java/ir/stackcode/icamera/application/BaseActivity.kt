package ir.stackcode.icamera.application

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.lang.reflect.Method


abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onUserInteraction() {
        //       if (userInteraction)
        (application as MyApplication).resetTimer()
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

//    fun setUserInteraction(userInteraction: Boolean) {
//        this.userInteraction = userInteraction
//        (application as MyApplication).cancelTimer()
//    }

//    private var userInteraction = true

}