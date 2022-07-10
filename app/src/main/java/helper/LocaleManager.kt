package helper

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import com.yariksoffice.lingver.Lingver
import java.util.Locale


class LocaleManager {
    companion object {
        fun setLocale(context: Context, language: String) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString("lang", language)
            editor.apply()
            Lingver.getInstance().setLocale(context, language)
        }

        fun getRealLanguage(context: Context) = PreferenceManager.getDefaultSharedPreferences(context).getString("lang", "en")!!
       // fun getRealLanguage(context: Context) = "fa"

        fun getLanguage() = Lingver.getInstance().getLanguage()
//        fun getLanguage() = "fa"

        fun getDirection() = when (getLanguage()) {
            "en" -> "ltr"
            else -> "rtl"
        }

        fun setActivityLocale(context: Context) {
            val language = getRealLanguage(context)
            Locale.setDefault(
                Locale(
                    language, if (language == "fa") "IR" else "US"
                )
            )
            Lingver.getInstance().setLocale(context, getRealLanguage(context))
        }
    }

    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.locale = locale
        res.updateConfiguration(config, res.displayMetrics)
    }
}