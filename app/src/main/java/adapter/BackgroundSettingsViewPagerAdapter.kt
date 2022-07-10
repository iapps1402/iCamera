package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import ir.stackcode.videoproject.databinding.LiMediaViewerBinding


class BackgroundSettingsViewPagerAdapter(
    val context: Context,
    private val drawables: ArrayList<Int>,
) : PagerAdapter() {
    override fun getCount() = drawables.size

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LiMediaViewerBinding.inflate(LayoutInflater.from(context), container, false)

        binding.image.setImageResource(drawables[position])

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }
}