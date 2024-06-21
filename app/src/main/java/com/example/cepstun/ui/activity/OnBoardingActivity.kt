package com.example.cepstun.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cepstun.databinding.ActivityOnBoardingBinding
import com.example.cepstun.viewModel.OnBoardingViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import java.util.Locale

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    private val viewModel: OnBoardingViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        viewModel.init()

        with(binding) {
            BLogin.setOnClickListener {
                Intent(this@OnBoardingActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
            BSignUp.setOnClickListener {
                Intent(this@OnBoardingActivity, ChooseUserActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
            IBLanguage.setOnClickListener {
                Intent(this@OnBoardingActivity, ChangeLanguageActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }

        }
    }
}