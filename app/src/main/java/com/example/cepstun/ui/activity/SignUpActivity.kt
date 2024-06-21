package com.example.cepstun.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivitySignUpBinding
import com.example.cepstun.viewModel.SignUpViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var level: String

    private val viewModel: SignUpViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    @Suppress("DEPRECATION")
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        callbackManager = CallbackManager.Factory.create()

        val load = binding.PBLoad
        val lottie = binding.LottieAV

        level = intent.getStringExtra(USER_LEVEL) ?: ""

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID)
            .requestEmail()
            .build()

        @Suppress("DEPRECATION")
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        with(binding){

            BRegister.backgroundTintList = ContextCompat.getColorStateList(this@SignUpActivity, R.color.gray2)

            ETEmail.doAfterTextChanged { text ->
                viewModel.email.value = text.toString()
            }

            ETPassword.doAfterTextChanged { text ->
                viewModel.password.value = text.toString()
            }

            ETPassword2.doAfterTextChanged { text ->
                viewModel.password2.value = text.toString()
            }

            viewModel.isFormValid.observe(this@SignUpActivity) { isValid ->
                BRegister.isEnabled = isValid
                BRegister.backgroundTintList =
                    ContextCompat.getColorStateList(this@SignUpActivity, if (isValid) R.color.brown else R.color.gray2)
            }

            BRegister.setOnClickListener {
                lottie.playAnimation()
                load.visibility = View.VISIBLE
                lifecycleScope.launch {
                    if (!isInternetAvailable()) {
                        showNoInternetDialog()
                        lottie.cancelAnimation()
                        load.visibility = View.GONE
                    } else {
                        viewModel.registerEmailPassword(ETEmail.text.toString().trim(), ETPassword.text.toString().trim(), level)
                    }
                }
            }

            TVLogin.setOnClickListener {
                moveToLogin()
            }


            MCVLoginGoogle.setOnClickListener{
                lottie.playAnimation()
                load.visibility = View.VISIBLE
                lifecycleScope.launch {
                    if (!isInternetAvailable()) {
                        showNoInternetDialog()
                        lottie.cancelAnimation()
                        load.visibility = View.GONE
                    } else {
                        resultLauncher.launch(googleSignInClient.signInIntent)
                    }
                }
            }

            MCVLoginFacebook.setOnClickListener {
                lottie.playAnimation()
                load.visibility = View.VISIBLE
                lifecycleScope.launch {
                    if (!isInternetAvailable()) {
                        showNoInternetDialog()
                        lottie.cancelAnimation()
                        load.visibility = View.GONE
                    } else {
                        LoginFacebook.performClick()
                    }
                }
            }

            arrayOf<String?>("email", "public_profile")
            LoginFacebook.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        lottie.cancelAnimation()
                        load.visibility = View.GONE
                        handleFacebookAccessToken(result.accessToken)
                    }

                    override fun onCancel() {
                        lottie.cancelAnimation()
                        load.visibility = View.GONE
                        Toast.makeText(
                            this@SignUpActivity,
                            "Login Facebook Canceled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: FacebookException) {
                        lottie.cancelAnimation()
                        load.visibility = View.GONE
                        Toast.makeText(
                            this@SignUpActivity,
                            "Login Facebook Failed: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
            )

        }


        viewModel.isLoading.observe(this){
            if (it){
                lottie.playAnimation()
                load.visibility = View.VISIBLE
            } else {
                lottie.cancelAnimation()
                load.visibility = View.GONE
            }
        }

        viewModel.message.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun isInternetAvailable(): Boolean = withContext(Dispatchers.IO) {
//        val runtime = Runtime.getRuntime()
//        return@withContext try {
//            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
//            val exitValue = ipProcess.waitFor()
//            exitValue == 0
//        } catch (e: IOException) {
//            e.printStackTrace()
//            false
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//            false
//        }
        return@withContext true
    }

    private fun showNoInternetDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Tidak Terhubung ke Internet")
            .setMessage("Aplikasi ini memerlukan koneksi internet untuk berfungsi. Silakan cek koneksi internet Anda.")
            .setPositiveButton("Oke") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun playAnimation() {
        val tittle = ObjectAnimator.ofFloat(binding.TVLoginTittle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.TVTittleEmail, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.ETEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.TVTittlePassword, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.ETPassword, View.ALPHA, 1f).setDuration(500)
        val password2 = ObjectAnimator.ofFloat(binding.TVTittlePassword2, View.ALPHA, 1f).setDuration(500)
        val etPassword2 = ObjectAnimator.ofFloat(binding.ETPassword2, View.ALPHA, 1f).setDuration(500)


        val together2 = AnimatorSet().apply {
            playTogether(email, password, password2)
        }

        val together3 = AnimatorSet().apply {
            playTogether(etEmail, etPassword, etPassword2)
        }

        AnimatorSet().apply {
            playSequentially(tittle, together2, together3)
            start()
        }

    }

    private fun moveToLogin() {
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@SignUpActivity,
                Pair(binding.IVLogo2, "logo2"),
                Pair(binding.BRegister, "button")
            )

        Intent(this@SignUpActivity , LoginActivity::class.java).also { intent ->
            startActivity(intent, optionsCompat.toBundle())
            ActivityCompat.finishAfterTransition(this)
        }
    }

    @Suppress("DEPRECATION")
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                GoogleSignIn.getClient(this, gso).signOut()
                val account = task.getResult(ApiException::class.java)!!
                viewModel.loginWithGoogle(account, level)
            } catch (e: ApiException) {
                Toast.makeText(this, "Login Failed with : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        LoginManager.getInstance().logOut()
        viewModel.loginWithFacebook(token, level)
    }

    companion object{
        const val USER_LEVEL: String = "user_level"
    }
}