package com.jiangdg.usbcamera.utils


import com.serenegiant.usb.DeviceFilter

object HelperCamera {

    fun getCameraUSBFilters(deviceFilters: List<DeviceFilter>): List<DeviceFilter> =
        //deviceFilters
    deviceFilters.filter { it.mVendorId != 4292 && it.mProductId != 60000  }
}