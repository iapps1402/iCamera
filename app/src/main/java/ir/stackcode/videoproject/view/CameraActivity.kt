package ir.stackcode.videoproject.view

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import ir.stackcode.videoproject.application.BaseActivity
import ir.stackcode.videoproject.databinding.ActivityCameraBinding
import ir.stackcode.videoproject.fragments.CameraFragment
import java.lang.reflect.Method

class CameraActivity : BaseActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var position = -1
    private lateinit var cameraFragment: CameraFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(binding.root)

        position = intent.getIntExtra("position", -1)

        initFragment()
       // setUserInteraction(false)
    }

    override fun onUSBNewData(data: String) {
    }

    private fun initFragment() {
        cameraFragment = CameraFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
                putBoolean("show_buttons", true)
            }

            setOnPositionChangeListener(object : CameraFragment.OnPositionChanged {
                override fun onChange(position: Int) {
                    this@CameraActivity.position = position
                    initFragment()
                }
            })
        }

        supportFragmentManager
            .beginTransaction()
            .replace(binding.cameraView.id, cameraFragment)
            .commit()
    }

    @SuppressLint("SoonBlockedPrivateApi")
    fun grantAutomaticUsbPermissionRoot(context: Context, usbDevice: UsbDevice): Boolean {
        return try {
            val pkgManager: PackageManager = context.packageManager
            val appInfo: ApplicationInfo =
                pkgManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod: Method =
                serviceManagerClass.getDeclaredMethod("getService", String::class.java)
            getServiceMethod.isAccessible = true
            val binder = getServiceMethod.invoke(null, Context.USB_SERVICE) as IBinder
            val iUsbManagerClass = Class.forName("android.hardware.usb.IUsbManager")
            val stubClass = Class.forName("android.hardware.usb.IUsbManager\$Stub")
            val asInterfaceMethod: Method =
                stubClass.getDeclaredMethod("asInterface", IBinder::class.java)
            asInterfaceMethod.isAccessible = true
            val iUsbManager: Any = asInterfaceMethod.invoke(null, binder)
            val grantDevicePermissionMethod: Method = iUsbManagerClass.getDeclaredMethod(
                "grantDevicePermission",
                UsbDevice::class.java,
                Int::class.javaPrimitiveType
            )
            grantDevicePermissionMethod.isAccessible = true
            grantDevicePermissionMethod.invoke(iUsbManager, usbDevice, appInfo.uid)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onBackPressed() {
        return
    }
}