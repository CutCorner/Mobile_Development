package com.example.cepstun.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityChangeLanguageBinding
import com.example.cepstun.viewModel.ChangeLanguageViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class ChangeLanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeLanguageBinding

    private val viewModel: ChangeLanguageViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    private lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        viewModel.languageSetting.observe(this) { language: String? ->
            if (language == null || language == "US" || language == "en"){
                enActive()
                val locale = Locale("en")
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
            } else if (language == "ID" || language == "id"){
                indoActive()
                val locale = Locale(language)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
            }
        }

        binding.apply {

            MCVIndonesia.setOnClickListener {
                indoActive()
                language = "id"
            }

            MCVEnglish.setOnClickListener {
                enActive()
                language = "en"
            }

            BSave.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.setLanguageSetting(language)

                    val alertDialogBuilder = AlertDialog.Builder(this@ChangeLanguageActivity)
                    alertDialogBuilder.setTitle(getString(R.string.language_successfully_changed))
                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder
                        .setMessage(getString(R.string.please_restart_the_app_to_change_the_language))
                        .setIcon(R.drawable.logo_icon_cir)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _->
                            Intent(this@ChangeLanguageActivity, SplashScreenActivity::class.java).also {
                                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(it)
                            }
                        }
                    val alertDialog = alertDialogBuilder.create()

                    alertDialog.show()
                }
            }

            IBBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }
    }

    private fun indoActive() {
        binding.MCVIndonesia.strokeColor = getColor(R.color.brown)
        binding.MCVEnglish.strokeColor = getColor(R.color.white)
    }

    private fun enActive() {
        binding.MCVEnglish.strokeColor = getColor(R.color.brown)
        binding.MCVIndonesia.strokeColor = getColor(R.color.white)
    }
}
