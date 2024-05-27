package com.example.cepstun.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.cepstun.databinding.ActivitySplashScreenBinding
import com.example.cepstun.viewModel.SplashScreenViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var auth: FirebaseAuth

    private val viewModel: SplashScreenViewModel by viewModels{
        ViewModelFactory.getInstance(this.application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth


        hideSystemUI()

        Handler(Looper.getMainLooper()).postDelayed({
            cekUserAlreadyLogin()
        }, SPLASH_SCREEN_DURATION)
    }

    private fun cekUserAlreadyLogin() {
        val isUserLoggedIn = viewModel.cekUserLogin()

        if (isUserLoggedIn){
            Intent(this, MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        } else {
            Intent(this, OnBoardingActivity::class.java).also { intent ->
                startActivity(intent)
                finish()
            }
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.IVStartLogo).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    companion object {
        private const val SPLASH_SCREEN_DURATION = 4000L
    }
}