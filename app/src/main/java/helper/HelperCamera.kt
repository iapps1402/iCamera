package helper

import com.jiangdg.usbcamera.UVCCameraHelper
import ir.stackcode.icamera.DeviceInfo

object HelperCamera {

    fun getUSBDevInfo(cameraHelper: UVCCameraHelper): List<DeviceInfo> {
        val devices: MutableList<DeviceInfo> = ArrayList()
        val list = cameraHelper.usbDeviceList
        for (dev in list) {
            val info = DeviceInfo()
            info.pid = dev.productId
            info.vid = dev.vendorId
            devices.add(info)
        }
        return devices
    }

    fun getCameraUSBevInfo(cameraHelper: UVCCameraHelper): List<DeviceInfo> {
        return getUSBDevInfo(cameraHelper).filter { it.pid != 60000 && it.vid != 4292 }
    }

}