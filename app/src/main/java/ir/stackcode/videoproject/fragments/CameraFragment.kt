package ir.stackcode.videoproject.fragments

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.*
import android.os.Looper.getMainLooper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.jiangdg.usbcamera.UVCCameraHelper
import com.jiangdg.usbcamera.UVCCameraHelper.OnMyDevConnectListener
import com.jiangdg.usbcamera.utils.FileUtils
import com.serenegiant.usb.CameraDialog
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.common.AbstractUVCCameraHandler.OnEncodeResultListener
import com.serenegiant.usb.encoder.RecordParams
import com.serenegiant.usb.widget.CameraViewInterface
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import helper.HelperCamera
import helper.HelperFile
import helper.HotKeys
import ir.stackcode.videoproject.AlertCustomDialog
import ir.stackcode.videoproject.DeviceInfo
import ir.stackcode.videoproject.R
import ir.stackcode.videoproject.application.BaseActivity
import ir.stackcode.videoproject.application.MyApplication
import ir.stackcode.videoproject.databinding.ActivityUsbcameraFragmentBinding

class CameraFragment : Fragment(), CameraDialog.CameraDialogParent, CameraViewInterface.Callback {

    private lateinit var mCameraHelper: UVCCameraHelper
    private lateinit var mUVCCameraView: CameraViewInterface
    private var voice = false

    private var position = -1
    private var isRequest = false
    private var isPreview = false

    private lateinit var binding: ActivityUsbcameraFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityUsbcameraFragmentBinding.inflate(layoutInflater)

        mUVCCameraView = binding.cameraView
        mUVCCameraView.setCallback(this)

        arguments?.let {
            it.containsKey("position")
            position = it.getInt("position")
        }

        binding.positionText.text = "${getString(R.string.camera_number)} " + " ${position + 1}"

        mCameraHelper = UVCCameraHelper().apply {
            setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG)
            initUSBMonitor(activity, mUVCCameraView, listener)
        }

        binding.exitLayout.setOnClickListener {
            showResolutionListDialog()
        }

        binding.settingsLayout.setOnClickListener {
            (activity?.application as MyApplication).blackenScreen()
            activity?.finish()
        }

        binding.switchRecVoice.setOnClickListener {
            voice = !voice
            binding.switchRecVoice.setImageResource(if (voice) R.drawable.volume_on else R.drawable.volume_mute)
            (activity as BaseActivity).writeData(if (voice) HotKeys.VOICE_ON else HotKeys.VOICE_OFF)
        }

        binding.keyLayout.setOnClickListener {
            binding.keyLayout.isEnabled = false
            binding.icKey.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )

            object : CountDownTimer(1200, 100) {
                override fun onTick(duration: Long) {
                }

                override fun onFinish() {
                    binding.keyLayout.isEnabled = true
                    binding.icKey.clearColorFilter()
                }
            }.start()

            (activity as BaseActivity).writeData(HotKeys.SEND_OPEN_DOOR)
        }

        binding.homeLayout.setOnClickListener {
            activity?.finish()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val display = requireActivity().windowManager.defaultDisplay
        binding.cameraView.layoutParams =
            ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, display.width)

        val showButtons = arguments?.getBoolean("show_buttons", true) ?: true

        if (showButtons) {
            binding.menu.visibility = View.VISIBLE
            binding.switchRecVoice.visibility = View.VISIBLE
        } else {
            binding.menu.visibility = View.GONE
            binding.switchRecVoice.visibility = View.GONE
        }

        binding.screenshotLayout.setOnClickListener {
            if (!mCameraHelper.isCameraOpened) {
                Toast.makeText(context, R.string.can_not_open_camera, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val picPath = (HelperFile.getFileDir() + "images/"
                    + System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG)

            mCameraHelper.capturePicture(picPath) { path: String ->
                if (TextUtils.isEmpty(path)) {
                    return@capturePicture
                }
                Handler(getMainLooper()).post {
                    Toast.makeText(context, R.string.saved, Toast.LENGTH_LONG).show()
                }
            }
        }

        var durationSeconds = 0
        val handler = Handler()
        binding.recordingLayout.setOnClickListener {
            if (!mCameraHelper.isCameraOpened) {
                Toast.makeText(
                    context,
                    R.string.could_not_find_any_camera_device,
                    Toast.LENGTH_LONG
                ).show()
                binding.time.visibility = View.VISIBLE

                handler.postDelayed({
                    binding.time.text = String.format(
                        "%02d:%02d", durationSeconds / 3600,
                        (durationSeconds % 3600) / 60, (durationSeconds % 60)
                    )

                }, 1000)

                return@setOnClickListener
            }

            if (!mCameraHelper.isPushing) {
                binding.time.visibility = View.INVISIBLE
                handler.removeCallbacksAndMessages(null);
                val videoPath =
                    (HelperFile.getFileDir() + "videos/" + System.currentTimeMillis()
                            + UVCCameraHelper.SUFFIX_MP4)

                val params = RecordParams()
                params.recordPath = videoPath
                params.recordDuration = 0

                params.isVoiceClose = voice


                params.isSupportOverlay = true

                mCameraHelper.startPusher(params, object : OnEncodeResultListener {
                    override fun onEncodeResult(
                        data: ByteArray,
                        offset: Int,
                        length: Int,
                        timestamp: Long,
                        type: Int
                    ) {

                        if (type == 1) {
                            FileUtils.putFileStream(data, offset, length)
                        }
                    }

                    override fun onRecordResult(videoPath: String) {
                        if (TextUtils.isEmpty(videoPath)) {
                            return
                        }
                    }
                })
                // if you only want to push stream,please call like this
                // mCameraHelper.startPusher(listener);

                Toast.makeText(context, R.string.record_starting, Toast.LENGTH_LONG).show()
                binding.recordIcon.setImageResource(R.drawable.ic_recording2)
                voice = false
                binding.switchRecVoice.setImageResource(R.drawable.volume_mute)
            } else {
                FileUtils.releaseFile()
                mCameraHelper.stopPusher()
                binding.recordIcon.setImageResource(R.drawable.ic_recording)
                Toast.makeText(context, R.string.saving_record, Toast.LENGTH_LONG).show()
                voice = true
                binding.switchRecVoice.setImageResource(R.drawable.volume_on)
            }
        }

        binding.cameraLayout.setOnClickListener {

            val infoList = HelperCamera.getUSBDevInfo(mCameraHelper)
            if (infoList.isEmpty()) {
                Toast.makeText(
                    context,
                    getString(R.string.could_not_find_any_camera_device),
                    Toast.LENGTH_SHORT
                )
                    .show()

                return@setOnClickListener
            }

            val dataList = java.util.ArrayList<String>()
            for (deviceInfo in infoList) {
                dataList.add(
                    getString(R.string.camera_number) + " " + (infoList.indexOf(
                        deviceInfo
                    ) + 1)
                )
            }


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
                .setOnMenuItemClickListener { position, _ ->
                    onPositionChanged?.onChange(position)

                }
                .build()

            menu.apply {
                setOnMenuItemClickListener { position, _ ->
                    onPositionChanged?.onChange(position)
                    menu.dismiss()
                }
                showAsAnchorCenter(binding.cameraLayout, 0, -100)
            }
        }
    }

    interface OnPositionChanged {
        fun onChange(position: Int)
    }

    private var onPositionChanged: OnPositionChanged? = null

    fun setOnPositionChangeListener(onChange: OnPositionChanged) {
        this.onPositionChanged = onChange
    }

    private fun popCheckDevDialog() {
        val infoList = getUSBDevInfo()
        if (infoList.isEmpty()) {
            Toast.makeText(context, R.string.could_not_find_any_camera_device, Toast.LENGTH_LONG)
                .show()
            return
        }
        val dataList: MutableList<String> = java.util.ArrayList()
        for (deviceInfo in infoList) {
            dataList.add("Device：PID_" + deviceInfo.pid + " & " + "VID_" + deviceInfo.vid)
        }
        AlertCustomDialog.createSimpleListDialog(
            context,
            getString(R.string.please_select_a_device),
            dataList
        ) { position ->
            mCameraHelper.requestPermission(
                position
            )
        }
    }

    private val listener: OnMyDevConnectListener = object : OnMyDevConnectListener {
        override fun onAttachDev(device: UsbDevice) {
            // request open permission
            if (!isRequest) {
                isRequest = true

                if (position != -1)
                    mCameraHelper.requestPermission(
                        position
                    )
                else
                    popCheckDevDialog()
            }
        }

        override fun onDettachDev(device: UsbDevice) {
            // close camera
            if (isRequest) {
                isRequest = false
                mCameraHelper.closeCamera()
                Toast.makeText(context, R.string.camera_detached, Toast.LENGTH_LONG).show()
            }
        }

        override fun onConnectDev(device: UsbDevice, isConnected: Boolean) {
            if (!isConnected) {
                Toast.makeText(context, R.string.failed_to_connect, Toast.LENGTH_LONG).show()
                isPreview = false
            } else {
                isPreview = true
                //Toast.makeText(context, R.string.preparing_camera, Toast.LENGTH_LONG).show()
                // initialize seekbar
                // need to wait UVCCamera initialize over
                Thread {
                    try {
                        Thread.sleep(2500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    Looper.prepare()
                    Looper.loop()
                }.start()
            }
        }

        override fun onDisConnectDev(device: UsbDevice) {
        }
    }

    @SuppressLint("CheckResult")
    private fun showResolutionListDialog() {
        val items = getResolutionList()
        MaterialDialog(requireActivity())
            .title(R.string.resolution)
            .show {
                listItems(items = items) { _, index, _ ->
                    if (!mCameraHelper.isCameraOpened) return@listItems
                    val tmp = items[index].split("x").toTypedArray()
                    if (tmp.size >= 2) {
                        val width = tmp[0].toInt()
                        val height = tmp[1].toInt()
                        mCameraHelper.updateResolution(width, height)
                    }
                }
            }
    }

    private fun getResolutionList(): List<String> {
        val list = mCameraHelper.supportedPreviewSizes
        val resolutions: ArrayList<String> = ArrayList()
        if (list != null && list.size != 0) {
            for (size in list) {
                if (size != null) {
                    resolutions.add(size.width.toString() + "x" + size.height)
                }
            }
        }
        return resolutions
    }

    override fun onStart() {
        super.onStart()
        mCameraHelper.registerUSB()
    }

    override fun onStop() {
        super.onStop()
        mCameraHelper.unregisterUSB()
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUtils.releaseFile()
        mCameraHelper.release()
    }


    private fun getUSBDevInfo(): List<DeviceInfo> {
        val devInfos: MutableList<DeviceInfo> = ArrayList()
        val list = mCameraHelper.usbDeviceList
        for (dev in list) {
            val info = DeviceInfo()
            info.pid = dev.vendorId
            info.vid = dev.productId
            devInfos.add(info)
        }
        return devInfos
    }

    override fun getUSBMonitor(): USBMonitor? {
        return mCameraHelper.usbMonitor
    }

    override fun onSurfaceCreated(view: CameraViewInterface?, surface: Surface?) {
        if (!isPreview && mCameraHelper.isCameraOpened) {
            mCameraHelper.startPreview(mUVCCameraView)
            isPreview = true
        }
    }

    override fun onSurfaceChanged(
        view: CameraViewInterface?,
        surface: Surface?,
        width: Int,
        height: Int
    ) {
    }

    override fun onSurfaceDestroy(view: CameraViewInterface?, surface: Surface?) {
        if (isPreview && mCameraHelper.isCameraOpened) {
            mCameraHelper.stopPreview()
            isPreview = false
        }
    }

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            requireActivity().finish()
        }
    }
}