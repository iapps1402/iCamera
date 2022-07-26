package ir.stackcode.videoproject.view

import android.os.Bundle
import android.view.WindowManager
import ir.stackcode.videoproject.application.BaseActivity
import ir.stackcode.videoproject.databinding.ActivityCamera4Binding
import ir.stackcode.videoproject.fragments.CameraFragment

class Camera4Activity : BaseActivity() {
    private lateinit var binding: ActivityCamera4Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCamera4Binding.inflate(layoutInflater)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(binding.root)

        initFragment()
    }

    private fun initFragment() {
        val cameraFragment1 = CameraFragment().apply {
            arguments = Bundle().apply {
                putInt("position", 0)
                putBoolean("show_buttons", false)
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(binding.cameraView1.id, cameraFragment1)
            .commit()


        val cameraFragment2 = CameraFragment().apply {
            arguments = Bundle().apply {
                putInt("position", 1)
                putBoolean("show_buttons", false)
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(binding.cameraView2.id, cameraFragment2)
            .commit()


        val cameraFragment3 = CameraFragment().apply {
            arguments = Bundle().apply {
                putInt("position", 2)
                putBoolean("show_buttons", false)
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(binding.cameraView3.id, cameraFragment3)
            .commit()


        val cameraFragment4 = CameraFragment().apply {
            arguments = Bundle().apply {
                putInt("position", 3)
                putBoolean("show_buttons", false)
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(binding.cameraView4.id, cameraFragment4)
            .commit()

        binding.returnToHome.setOnClickListener {
            finish()
            //startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onBackPressed() {
        return
    }
}