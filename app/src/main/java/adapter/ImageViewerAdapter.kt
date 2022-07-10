package adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import ir.stackcode.videoproject.databinding.ImageViewerItemBinding
import java.io.File


class ImageViewerAdapter(
    val context: Context,
    private val images: ArrayList<File>,
) : PagerAdapter() {
    override fun getCount() = images.size

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ImageViewerItemBinding.inflate(LayoutInflater.from(context), container, false)

        binding.image.setImageURI(images[position].toUri())

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }
}