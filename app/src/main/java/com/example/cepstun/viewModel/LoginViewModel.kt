package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.ui.activity.ChooseUserActivity
import com.example.cepstun.ui.activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _showProgressDialog = MutableLiveData<Boolean>()
    val showProgressDialog: MutableLiveData<Boolean> = _showProgressDialog

    private val _showToast = MutableLiveData<String>()
    val showToast: MutableLiveData<String> = _showToast

    // for validate TextView
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val isEmailValid = email.map { it?.isNotEmpty() == true }
    private val isPasswordValid = password.map { it?.isNotEmpty() == true }


    val isFormValid = MediatorLiveData<Boolean>().apply {
        addSource(isEmailValid) { value = validateForm() }
        addSource(isPasswordValid) { value = validateForm() }
    }


    private fun validateForm(): Boolean {
        return isEmailValid.value == true && isPasswordValid.value == true
    }

    fun loginEmailPassword(email: String, password: String) {
        _showProgressDialog.value = true
        viewModelScope.launch {
            val (isSuccessful, message) = repositoryAuth.loginEmailPassword(email, password, context.getString(R.string.login_success), context.getString(R.string.not_verified), context.getString(R.string.login_failed_with_message))
            if (isSuccessful){
                _showProgressDialog.value = false
                _showToast.value = message
                Intent(context, MainActivity::class.java).also { intent ->
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            } else{
                _showToast.value = message
                _showProgressDialog.value = false
            }
        }
    }

    fun loginWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            val credential = repositoryAuth.loginWithGoogle(account)
            if (credential.first) {
                val user = repositoryAuth.auth.currentUser
                if (user != null) {
                    if (repositoryDatabase.checkUserExists(user.uid)){
                        _showProgressDialog.value = false
                        Intent(
                            context, MainActivity::class.java
                        ).also { intent ->
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    } else {
                        _showProgressDialog.value = false
                        Intent(
                            context, ChooseUserActivity::class.java
                        ).also { intent ->
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra(
                                ChooseUserActivity.ID_TOKEN, credential.second
                            )
                            context.startActivity(intent)
                        }
                    }
                } else {
                    _showProgressDialog.value = false
                    _showToast.value = context.getString(R.string.fail_connect_data)
                }
            } else {
                _showProgressDialog.value = false
                _showToast.value = context.getString(R.string.fail_connect_data)
            }
        }
    }


    fun resendEmail(email: String, password: String) {

        if (email.isEmpty() && password.isEmpty()) {
            _showToast.value = context.getString(R.string.password_email_empty)

        } else {
            viewModelScope.launch {
                _showToast.value = repositoryAuth.resendEmail(email, password, context.getString(R.string.send_email_verif), context.getString(R.string.verified_email), context.getString(R.string.fail_send_email))
            }
        }
    }

    fun forgetPassword(email: String) {
        if (email.isEmpty()) {
            _showToast.value = context.getString(R.string.email_empty)
        } else {
            viewModelScope.launch {
                _showToast.value = repositoryAuth.forgetPassword(email, context.getString(R.string.email_reset_password))
            }
        }
    }

}