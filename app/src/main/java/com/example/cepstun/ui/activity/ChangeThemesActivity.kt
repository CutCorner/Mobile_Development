package com.example.cepstun.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityChangeThemesBinding
import com.example.cepstun.viewModel.ChangeThemesViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeThemesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeThemesBinding

    private val viewModel: ChangeThemesViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    private var dark: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeThemesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.themeSetting.observe(this){isDarkModeActive: Boolean? ->
            if (isDarkModeActive == null || isDarkModeActive == false) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                lightActive()
                dark = false
            } else if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                darkActive()
                dark = true
            }
        }

        binding.apply {
            MCVDark.setOnClickListener {
                darkActive()
                dark = true
            }

            MCVBright.setOnClickListener {
                lightActive()
                dark = false
            }

            BSave.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    launch(Dispatchers.Main) {
                        PBLoad.visibility = View.VISIBLE
                        LottieAV.playAnimation()
                    }
                    viewModel.setThemeSetting(dark)
                    launch(Dispatchers.Main) {
                        PBLoad.visibility = View.GONE
                        LottieAV.cancelAnimation()
                    }
                }
            }

            IBBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

//        viewModel.isLoading.observe(this) {
//            val load = binding.PBLoad
//            val lottie = binding.LottieAV
//            if (it) {
//                load.visibility = View.VISIBLE
//                lottie.playAnimation()
//            } else {
//                load.visibility = View.GONE
//                lottie.cancelAnimation()
//            }
//        }

    }

    private fun darkActive() {
        binding.MCVDark.strokeColor = getColor(R.color.brown)
        binding.MCVBright.strokeColor = getColor(R.color.white)
    }

    private fun lightActive() {
        binding.MCVBright.strokeColor = getColor(R.color.brown)
        binding.MCVDark.strokeColor = getColor(R.color.white)
    }
}
