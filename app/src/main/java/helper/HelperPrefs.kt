package helper

import android.content.Context
import android.preference.PreferenceManager

class HelperPrefs(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun setStorage(storage: Int) {
        val edit = prefs.edit()
        edit.putInt("storage", storage)
        edit.apply()
    }

    fun getStorage() = prefs.getInt("storage", EXTERNAL_STORAGE);

    companion object {
        const val INTERNAL_STORAGE = 0
        const val EXTERNAL_STORAGE = 1
    }
}