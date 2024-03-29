package ir.stackcode.icamera.view

import android.os.Bundle
import com.zeuskartik.mediaslider.MediaSliderActivity
import helper.HelperFile
import ir.stackcode.icamera.databinding.ActivityMediaBinding
import java.io.File

class MediaActivity : MediaSliderActivity() {
    private lateinit var binding: ActivityMediaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val files = ArrayList<File>()
        val dir =
            File(HelperFile.getFileDir() + "images")
        if (dir.isDirectory)
            dir.list()?.let {
                for (i in it.indices)
                    files.add(File(dir, it[i]))
            }

        loadMediaSliderView(files,"image",true,true,false,"Image-Slider","#000000",null,0);
    }
}