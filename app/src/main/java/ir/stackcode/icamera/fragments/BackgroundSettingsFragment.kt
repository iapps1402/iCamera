package ir.stackcode.icamera.fragments

import adapter.BackgroundSettingsViewPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import helper.LocaleManager
import ir.stackcode.icamera.R
import ir.stackcode.icamera.databinding.BackgroundSettingsFragmentBinding
import ir.stackcode.icamera.view.MainActivity


class BackgroundSettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BackgroundSettingsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawables = ArrayList<Int>().apply {
            add(R.drawable.wallpaper_1)
            add(R.drawable.wallpaper_2)
            add(R.drawable.wallpaper_3)
            add(R.drawable.wallpaper_4)
            add(R.drawable.wallpaper_5)
            add(R.drawable.wallpaper_6)
        }

        binding.viewPager.adapter = BackgroundSettingsViewPagerAdapter(requireContext(), drawables)

        binding.exitLayout.setOnClickListener {
            (activity as MainActivity).openSettings()
        }

        when (LocaleManager.getDirection()) {
            "rtl" -> {
                binding.next.setImageResource(R.drawable.next)
                binding.previous.setImageResource(R.drawable.previous)

                binding.next.setOnClickListener {
                    binding.viewPager.currentItem = binding.viewPager.currentItem + 1
                }

                binding.previous.setOnClickListener {
                    binding.viewPager.currentItem = binding.viewPager.currentItem - 1
                }

            }

            "ltr" -> {
                binding.next.setImageResource(R.drawable.previous)
                binding.previous.setImageResource(R.drawable.next)

                binding.previous.setOnClickListener {
                    binding.viewPager.currentItem = binding.viewPager.currentItem + 1
                }

                binding.next.setOnClickListener {
                    binding.viewPager.currentItem = binding.viewPager.currentItem - 1
                }
            }
        }

        binding.confirmLayout.setOnClickListener {
            context?.let {
                val prefs = it.getSharedPreferences("prefs", 0)
                val edit = prefs.edit()

                edit.putInt(
                    "wallpaper_id", when (binding.viewPager.currentItem) {
                        0 -> R.drawable.wallpaper_1
                        1 -> R.drawable.wallpaper_2
                        2 -> R.drawable.wallpaper_3
                        3 -> R.drawable.wallpaper_4
                        4 -> R.drawable.wallpaper_5
                        5 -> R.drawable.wallpaper_6
                        else -> R.drawable.wallpaper_1
                    }
                )
                edit.apply()

                (activity as MainActivity).notifyBackgroundChanged()
                (activity as MainActivity).openSettings()
            }
        }
    }

    private lateinit var binding: BackgroundSettingsFragmentBinding
}