package ir.stackcode.videoproject.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.jiangdg.usbcamera.UVCCameraHelper
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import helper.HelperCamera
import helper.HelperFile
import helper.LocaleManager
import ir.stackcode.videoproject.R
import ir.stackcode.videoproject.application.MyApplication
import ir.stackcode.videoproject.databinding.FragmentHomeBinding
import ir.stackcode.videoproject.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var mCameraHelper: UVCCameraHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -2)

        if (LocaleManager.getLanguage() == "fa") {
            binding.persianCalendar.visibility = View.VISIBLE
            binding.calendarView.visibility = View.GONE
        } else {
            binding.persianCalendar.visibility = View.GONE
            binding.calendarView.visibility = View.VISIBLE
        }

        mCameraHelper = UVCCameraHelper().apply {
            initUSBMonitor(activity, null, listener)
        }

        binding.exitLayout.setOnClickListener {
            (activity as MainActivity).let {
                (it.application as MyApplication).blackenScreen()
                activity?.finish()
            }
        }

        binding.galleryLayout.setOnClickListener {
            startActivity(Intent(requireContext(), ImageViewerActivity::class.java))
        }

        binding.settingsLayout.setOnClickListener {
            (activity as MainActivity).openSettings()
        }

        binding.contactLayout.setOnClickListener {
            (activity as MainActivity).openContact()
        }

        binding.cameraLayout.setOnClickListener {
            val infoList = HelperCamera.getCameraUSBevInfo(mCameraHelper)
            if (infoList.isEmpty()) {
                Toast.makeText(
                    activity,
                    getString(R.string.could_not_find_any_camera_device),
                    Toast.LENGTH_SHORT
                )
                    .show()

                return@setOnClickListener
            }

            val dataList = ArrayList<String>()
            for (deviceInfo in infoList) {
                dataList.add(
                    getString(R.string.camera_number) + " " + (infoList.indexOf(
                        deviceInfo
                    ) + 1)
                )
            }

            dataList.add(("4 * 1"))

            val menu = PowerMenu.Builder(requireActivity())
                .addItemList(dataList.map { PowerMenuItem(it, false) })
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextSize(21)
                //   .setTextColor(ContextCompat.getColor(context, R.color.md_grey_800))
                .setTextGravity(Gravity.CENTER)
                //  .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                // .setSelectedMenuColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .build()

            menu.apply {
                showAsAnchorCenter(binding.cameraLayout, 0, -100)
                setOnMenuItemClickListener { position, _ ->
                    if (position == dataList.size - 1) {
                        startActivity(Intent(activity, Camera4Activity::class.java))
                        return@setOnMenuItemClickListener
                    }

                    startActivity(Intent(activity, CameraActivity::class.java).apply {
                        putExtra("position", position)
                    })

                    dismiss()
                }
            }
        }

        setTime()
        job = lifecycleScope.launch {
            timer()
        }
    }

    private lateinit var job: Job
    private var flag = false
    private suspend fun timer() {
        delay(1000)
        setTime()
        flag = !flag
        timer()
    }

    private fun setTime() {
        binding.timeDoubleQuotation.visibility = if (flag) View.VISIBLE else View.INVISIBLE
        binding.timeHour.text = SimpleDateFormat("HH").format(Date())
        binding.timeMinute.text = SimpleDateFormat("mm").format(Date())
    }

    override fun onStart() {
        super.onStart()
        mCameraHelper.registerUSB()
    }

    override fun onStop() {
        super.onStop()
        mCameraHelper.unregisterUSB()
        job.cancel()
    }

    override fun onDetach() {
        super.onDetach()
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCameraHelper.release()
        job.cancel()
    }

    private val listener: UVCCameraHelper.OnMyDevConnectListener = object :
        UVCCameraHelper.OnMyDevConnectListener {
        override fun onAttachDev(device: UsbDevice) {
        }

        override fun onDettachDev(device: UsbDevice) {
            mCameraHelper.closeCamera()
        }

        override fun onConnectDev(device: UsbDevice?, isConnected: Boolean) {

        }

        override fun onDisConnectDev(device: UsbDevice?) {

        }

        private lateinit var mCameraHelper: UVCCameraHelper
    }

    private lateinit var binding: FragmentHomeBinding
}