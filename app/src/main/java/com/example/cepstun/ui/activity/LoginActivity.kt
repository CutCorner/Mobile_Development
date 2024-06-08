package com.example.cepstun.ui.activity

//import com.example.cepstun.BuildConfig
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityLoginBinding
import com.example.cepstun.viewModel.LoginViewModel
import com.example.cepstun.viewModel.ViewModelFactory
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }

    @Suppress("DEPRECATION")
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        playAnimation()

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.tittle_dialog_Loading))
        builder.setMessage(getString(R.string.desc_dialog_Login))
        builder.setCancelable(false)
        val dialog = builder.create()


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID)
            .requestEmail().build()

        @Suppress("DEPRECATION")
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        callbackManager = CallbackManager.Factory.create()

        with(binding) {
            BLogin.backgroundTintList =
                ContextCompat.getColorStateList(this@LoginActivity, R.color.gray2)

            TVSIgnUp.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@LoginActivity,
                        Pair(binding.IVLogo2, "logo2"),
                        Pair(binding.BLogin, "button")
                    )

                Intent(this@LoginActivity, ChooseUserActivity::class.java).also { intent ->
                    startActivity(intent, optionsCompat.toBundle())
                }
            }
            TVForget.setOnClickListener {
                viewModel.forgetPassword(ETEmail.text.toString().trim())
            }
            TVResend.setOnClickListener {
                viewModel.resendEmail(
                    ETEmail.text.toString().trim(), ETPassword.text.toString().trim()
                )
            }

            // Update the ViewModel when text changes
            ETEmail.doAfterTextChanged { text ->
                viewModel.email.value = text.toString()
            }

            ETPassword.doAfterTextChanged { text ->
                viewModel.password.value = text.toString()
            }

            viewModel.isFormValid.observe(this@LoginActivity) { isValid ->
                BLogin.isEnabled = isValid
                BLogin.backgroundTintList = ContextCompat.getColorStateList(
                    this@LoginActivity, if (isValid) R.color.brown else R.color.gray2
                )
            }

            BLogin.setOnClickListener {
                dialog.show()
                lifecycleScope.launch {
                    if (!isInternetAvailable()) {
                        showNoInternetDialog()
                        dialog.dismiss()
                    } else {
                        viewModel.loginEmailPassword(
                            ETEmail.text.toString().trim(), ETPassword.text.toString().trim()
                        )
                    }
                }
            }

            MCVLoginGoogle.setOnClickListener {
                dialog.show()
                lifecycleScope.launch {
                    if (!isInternetAvailable()) {
                        showNoInternetDialog()
                        dialog.dismiss()
                    } else {
                        resultLauncher.launch(googleSignInClient.signInIntent)
                    }
                }
            }

            MCVLoginFacebook.setOnClickListener {
                lifecycleScope.launch {
                    if (!isInternetAvailable()) {
                        showNoInternetDialog()
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
                        dialog.dismiss()
                        handleFacebookAccessToken(result.accessToken)
                    }

                    override fun onCancel() {
                        dialog.dismiss()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Facebook Canceled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: FacebookException) {
                        dialog.dismiss()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Facebook Failed: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
            )

            MCVLoginApple.setOnClickListener {
                comingSoon()
            }

            onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Intent(this@LoginActivity, OnBoardingActivity::class.java).also { intent ->
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            })
        }

        viewModel.showProgressDialog.observe(this) {
            if (!it) {
                dialog.dismiss()
            } else {
                dialog.show()
            }
        }

        viewModel.showToast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun isInternetAvailable(): Boolean = withContext(Dispatchers.IO) {
        val runtime = Runtime.getRuntime()
        return@withContext try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

    private fun showNoInternetDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Tidak Terhubung ke Internet")
            .setMessage("Aplikasi ini memerlukan koneksi internet untuk berfungsi. Silakan cek koneksi internet Anda.")
            .setPositiveButton("Oke") { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false).show()
    }

    @Suppress("DEPRECATION")
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                GoogleSignIn.getClient(this, gso).signOut()
                val account = task.getResult(ApiException::class.java)!!
                viewModel.loginWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(
                    this, "Login Failed with : ${task.exception?.message}", Toast.LENGTH_SHORT
                ).show()
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
        viewModel.loginWithFacebook(token)
    }

    private fun playAnimation() {
        val tittle = ObjectAnimator.ofFloat(binding.TVLoginTittle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.TVTittleEmail, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.ETEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.TVTittlePassword, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.ETPassword, View.ALPHA, 1f).setDuration(500)
        val resend = ObjectAnimator.ofFloat(binding.TVResend, View.ALPHA, 1f).setDuration(500)
        val forgot = ObjectAnimator.ofFloat(binding.TVForget, View.ALPHA, 1f).setDuration(500)

        val together1 = AnimatorSet().apply {
            playTogether(resend, forgot)
        }

        val together2 = AnimatorSet().apply {
            playTogether(email, password)
        }

        val together3 = AnimatorSet().apply {
            playTogether(etEmail, etPassword)
        }

        AnimatorSet().apply {
            playSequentially(tittle, together2, together3, together1)
            start()
        }
    }

    private fun comingSoon() {
        Toast.makeText(this, getString(R.string.comingsoon), Toast.LENGTH_SHORT).show()
    }
}