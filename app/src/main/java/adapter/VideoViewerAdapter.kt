package adapter

import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import ir.stackcode.videoproject.databinding.VideoViewerItemBinding
import ir.stackcode.videoproject.view.VideoPlayerActivity
import java.io.File


class VideoViewerAdapter(
    val context: Context,
    private val videos: ArrayList<File>,
) : PagerAdapter() {
    override fun getCount() = videos.size

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = VideoViewerItemBinding.inflate(LayoutInflater.from(context), container, false)

        val bitmapThumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ThumbnailUtils.createVideoThumbnail(
                File(videos[position].toURI()),
                Size(300, 300),
                null
            )
        } else {
            ThumbnailUtils.createVideoThumbnail(
                videos[position].path,
                MediaStore.Images.Thumbnails.MINI_KIND
            )
        }

        binding.play.setOnClickListener {
            context.startActivity(Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra("path", videos[position].toURI().toString())
            })
        }

        binding.image.setImageBitmap(bitmapThumbnail)

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }
}