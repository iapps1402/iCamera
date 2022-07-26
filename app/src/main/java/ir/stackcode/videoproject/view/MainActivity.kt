package ir.stackcode.videoproject.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.Fragment
import ir.stackcode.videoproject.R
import ir.stackcode.videoproject.application.BaseActivity
import ir.stackcode.videoproject.databinding.ActivityMain2Binding
import ir.stackcode.videoproject.fragments.BackgroundSettingsFragment
import ir.stackcode.videoproject.fragments.ContactFragment
import ir.stackcode.videoproject.fragments.HomeFragment
import ir.stackcode.videoproject.fragments.SettingsFragment
import ir.stackcode.videoproject.utils.PermissionUtil
import ir.stackcode.videoproject.utils.PermissionUtil.COMMAND_CHOWN_USB_FILE
import ir.stackcode.videoproject.utils.PermissionUtil.COMMAND_COPY_USB_FILE
import ir.stackcode.videoproject.utils.RootUtil
import java.lang.reflect.Method

class MainActivity : BaseActivity() {
    private lateinit var currentFragment: Fragment
    private lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        currentFragment = HomeFragment()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, currentFragment)
            .commit()

        notifyBackgroundChanged()


        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager //USB initialization

        val deviceList = usbManager.deviceList
        for (usbDevice in deviceList.values) {

            grantAutomaticUsbPermissionRoot(this, usbDevice)
            val hasPermission = usbManager.hasPermission(usbDevice)
            // Log if USB manager explicitly reports no permission.
            if (!hasPermission) {
                Log.i("DARRAN", "USB Manager reporting no permission to reader.")
                val deviceFilter = PermissionUtil.DeviceFilter(usbDevice)
                writeSettingsFile(deviceFilter)
            }
        }
    }

    private fun writeSettingsFile(deviceFilter: PermissionUtil.DeviceFilter) {
        PermissionUtil.writeSettingsLocked(applicationContext, deviceFilter)
        RootUtil.executeAsRoot(COMMAND_COPY_USB_FILE)
        RootUtil.executeAsRoot(COMMAND_CHOWN_USB_FILE)
        // RootUtil.executeAsRoot("reboot")
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

    fun openSettings() {
        currentFragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, currentFragment)
            .commit()
    }

    fun openContact() {
        currentFragment = ContactFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, currentFragment)
            .commit()
    }

    override fun onBackPressed() {
//        if (currentFragment is HomeFragment) {
//            (application as MyApplication).blackenScreen()
//            return
//        }
//
//        if (currentFragment is SettingsFragment
//            || currentFragment is ContactFragment
//        ) {
//            currentFragment = HomeFragment()
//            supportFragmentManager.beginTransaction()
//                .replace(binding.container.id, currentFragment)
//                .commit()
//        }

        return
    }


    fun openHome() {
        if (currentFragment is HomeFragment)
            return

        currentFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, currentFragment)
            .commit()
    }

    fun openBackgroundSettings() {
        if (currentFragment is HomeFragment)
            return

        currentFragment = BackgroundSettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, currentFragment)
            .commit()
    }

    fun notifyBackgroundChanged() {
        val prefs = getSharedPreferences("prefs", 0)
        binding.wallpaper.setImageResource(prefs.getInt("wallpaper_id", R.drawable.wallpaper_1))
    }
}