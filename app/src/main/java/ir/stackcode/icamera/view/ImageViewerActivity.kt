package ir.stackcode.icamera.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import helper.HotKeys
import ir.stackcode.icamera.databinding.ActivityImageViewerBinding
import ir.stackcode.icamera.fragments.ViewerImage
import ir.stackcode.icamera.fragments.ViewerVideo

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.imageIcon.setColorFilter(Color.BLUE)
        binding.imageText.setTextColor(Color.BLUE)
        binding.videoIcon.setColorFilter(Color.WHITE)
        binding.homeIcon.setColorFilter(Color.WHITE)

        binding.homeLayout.setOnClickListener {
            finish()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(binding.container.id, ViewerImage())
            .commit()

        binding.imageIcon.setOnClickListener {
            binding.imageIcon.setColorFilter(Color.BLUE)
            binding.imageText.setTextColor(Color.BLUE)
            binding.videoIcon.setColorFilter(Color.WHITE)
            binding.videoText.setTextColor(Color.WHITE)

            supportFragmentManager
                .beginTransaction()
                .replace(binding.container.id, ViewerImage())
                .commit()
        }

        binding.videoIcon.setOnClickListener {
            binding.imageIcon.setColorFilter(Color.WHITE)
            binding.imageText.setTextColor(Color.WHITE)
            binding.videoIcon.setColorFilter(Color.BLUE)
            binding.videoText.setTextColor(Color.WHITE)

            supportFragmentManager
                .beginTransaction()
                .replace(binding.container.id, ViewerVideo())
                .commit()
        }
    }

    override fun onBackPressed() {
        return
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, mIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
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
                startActivity(Intent(this@ImageViewerActivity, CameraActivity::class.java).apply {
                    putExtra("position", position)
                })
                finish()
            }
        }
    }
}