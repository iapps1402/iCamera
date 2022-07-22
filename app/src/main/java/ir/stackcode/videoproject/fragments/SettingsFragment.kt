package ir.stackcode.videoproject.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import helper.HelperFile
import helper.HelperPrefs
import helper.LocaleManager
import ir.stackcode.videoproject.R
import ir.stackcode.videoproject.application.BaseActivity
import ir.stackcode.videoproject.databinding.FragmentSettingsBinding
import ir.stackcode.videoproject.view.MainActivity
import java.io.File

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exitLayout.setOnClickListener {
            (activity as MainActivity).openHome()
        }

        binding.backgroundLayout.setOnClickListener {
            (activity as MainActivity).openBackgroundSettings()
        }

        binding.translationLayout.setOnClickListener {
            if ((activity as BaseActivity).canOpenDialog()) {
                MaterialDialog(requireActivity())
                    .title(R.string.select_language)
                    .show {
                        (activity as BaseActivity).setFocusView(this.view)
                        listItems(R.array.languages) { _, index, _ ->
                            LocaleManager.setLocale(
                                requireActivity(), when (index) {
                                    0 -> "fa"
                                    else -> "en"
                                }
                            )
                            (activity as BaseActivity).releaseFocusView()
                            activity?.finishAffinity()
                            startActivity(Intent(activity, MainActivity::class.java))
                        }
                    }
            }
        }

        binding.trashLayout.setOnClickListener {
            if ((activity as BaseActivity).canOpenDialog()) {
                MaterialDialog(requireActivity())
                    .title(R.string.remove_data)
                    .show {
                        (activity as BaseActivity).setFocusView(this.view)
                        listItems(R.array.remove_files) { _, index, _ ->
                            when (index) {
                                0 -> {
                                    val dir =
                                        File(HelperFile.getFileDir() + "images")
                                    if (dir.isDirectory)
                                        dir.list()?.let {
                                            for (i in it.indices)
                                                File(dir, it[i]).delete()
                                        }

                                    Toast.makeText(
                                        requireContext(),
                                        R.string.removed_successfully,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                1 -> {
                                    val dir =
                                        File(HelperFile.getFileDir() + "videos")
                                    if (dir.isDirectory)
                                        dir.list()?.let {
                                            for (i in it.indices)
                                                File(dir, it[i]).delete()
                                        }

                                    Toast.makeText(
                                        requireContext(),
                                        R.string.removed_successfully,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            (activity as BaseActivity).releaseFocusView()
                        }
                    }
            }
        }

        binding.memoryLayout.setOnClickListener {
            if ((activity as BaseActivity).canOpenDialog()) {

                MaterialDialog(requireActivity())
                    .title(R.string.where_to_save_photos)
                    .show {
                        (activity as BaseActivity).setFocusView(this.view)
                        listItems(
                            items = if (HelperFile.isSdCardPresent()) listOf(getString(R.string.internal_storage)) else listOf(
                                getString(R.string.internal_storage),
                                getString(R.string.external_storage)
                            )
                        ) { _, index, _ ->
                            (activity as BaseActivity).releaseFocusView()
                            HelperPrefs(requireContext()).setStorage(if (index == 0) HelperPrefs.INTERNAL_STORAGE else HelperPrefs.EXTERNAL_STORAGE)
                            Toast.makeText(
                                requireContext(),
                                R.string.storage_path_changed,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private lateinit var binding: FragmentSettingsBinding
}