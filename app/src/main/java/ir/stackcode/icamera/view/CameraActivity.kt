package ir.stackcode.icamera.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import helper.HotKeys
import ir.stackcode.icamera.application.BaseActivity
import ir.stackcode.icamera.databinding.ActivityCameraBinding
import ir.stackcode.icamera.fragments.CameraFragment

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

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, mIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBackPressed() {
        return
    }

    private val mIntentFilter = IntentFilter("data_received")
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, intent: Intent) {
            val position = when (intent.getStringExtra("data_received")) {
                HotKeys.RECEIVE_OPEN_FIRST_CAMERA -> 0
                HotKeys.RECEIVE_OPEN_SECOND_CAMERA -> 1
                else -> -1
            }

            if(position != -1) {
                startActivity(Intent(this@CameraActivity, CameraActivity::class.java).apply {
                    putExtra("position", position)
                })
                finish()
            }
        }
    }
}