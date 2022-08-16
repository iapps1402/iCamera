package ir.stackcode.icamera.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.stackcode.icamera.R
import ir.stackcode.icamera.application.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity: BaseActivity() {
    private val REQUIRED_PERMISSION_LIST = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    private val REQUEST_CODE = 1
    private val mMissPermissions: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        if (isVersionM()) {
            checkAndRequestPermissions()
        } else {
            startMainActivity()
        }
    }

    private fun isVersionM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun checkAndRequestPermissions() {
        mMissPermissions.clear()
        for (permission in REQUIRED_PERMISSION_LIST) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                mMissPermissions.add(permission)
            }
        }
        // check permissions has granted
        if (mMissPermissions.isEmpty()) {
            startMainActivity()
        } else {
            ActivityCompat.requestPermissions(
                this,
                mMissPermissions.toTypedArray(),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            for (i in grantResults.indices.reversed()) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMissPermissions.remove(permissions[i])
                }
            }
        }
        // Get permissions success or not
        if (mMissPermissions.isEmpty()) {
            startMainActivity()
        } else {
            Toast.makeText(
                this@SplashActivity,
                "get permissions failed,exiting...",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun startMainActivity() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity2::class.java))
            finish()
        }, 3000)
    }
}