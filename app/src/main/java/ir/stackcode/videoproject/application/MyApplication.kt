package ir.stackcode.videoproject.application

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.CountDownTimer
import com.yariksoffice.lingver.Lingver
import helper.LocaleManager
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import ir.stackcode.videoproject.R
import ir.stackcode.videoproject.view.BlackActivity
import java.util.*

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
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
    }

    fun blackenScreen() {
        startActivity(Intent(this@MyApplication, BlackActivity::class.java).apply {
            flags = FLAG_ACTIVITY_CLEAR_TOP
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
}