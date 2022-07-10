package ir.stackcode.videoproject.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ScreenOffAdminReceiver : DeviceAdminReceiver() {
    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onEnabled(context: Context, intent: Intent) {
      Toast.makeText(context, "enabled", Toast.LENGTH_LONG).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "disabled", Toast.LENGTH_LONG).show()
    }
}