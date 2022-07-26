package ir.stackcode.videoproject.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ir.stackcode.videoproject.R
import ir.stackcode.videoproject.application.BaseActivity
import ir.stackcode.videoproject.databinding.ActivityMainBinding
import ir.stackcode.videoproject.fragments.CameraFragment

class MainActivity2 : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cam1, CameraFragment())
            .commit()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cam2, CameraFragment())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toobar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.url))
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }
}