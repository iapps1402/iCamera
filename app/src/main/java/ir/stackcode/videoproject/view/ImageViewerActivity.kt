package ir.stackcode.videoproject.view

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import ir.stackcode.videoproject.databinding.ActivityImageViewerBinding
import ir.stackcode.videoproject.fragments.ViewerImage
import ir.stackcode.videoproject.fragments.ViewerVideo

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
}