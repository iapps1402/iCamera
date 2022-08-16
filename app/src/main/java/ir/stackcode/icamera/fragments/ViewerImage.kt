package ir.stackcode.icamera.fragments

import adapter.ImageViewerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import helper.HelperFile
import ir.stackcode.icamera.databinding.ViewerFragmentBinding
import java.io.File

class ViewerImage : Fragment() {
    private lateinit var binding: ViewerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewerFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val files = ArrayList<File>()
        val dir =
            File(HelperFile.getFileDir() + "images")
        if (dir.isDirectory)
            dir.list()?.let {
                for (i in it.indices)
                    files.add(File(dir, it[i]))
            }

        if (files.size == 0) {
            binding.textNotFound.visibility = View.VISIBLE
        } else {
            binding.textNotFound.visibility = View.GONE
            val adapter = ImageViewerAdapter(requireContext(), files)
            binding.viewPager.adapter = adapter
        }
    }
}