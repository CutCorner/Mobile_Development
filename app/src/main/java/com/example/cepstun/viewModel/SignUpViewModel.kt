package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.ui.activity.LoginActivity
import com.example.cepstun.ui.activity.MainActivity
import com.example.cepstun.ui.activity.RegisterBarberActivity
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    application: Application
) : AndroidViewModel(application){

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _showProgressDialog = MutableLiveData<Boolean>()
    val showProgressDialog: MutableLiveData<Boolean> = _showProgressDialog

    private val _showToast = MutableLiveData<String>()
    val showToast: MutableLiveData<String> = _showToast

    // for validate TextView
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val password2 = MutableLiveData<String>()

    private val isEmailValid = email.map { it?.isNotEmpty() == true }
    private val isPasswordValid = password.map { it?.isNotEmpty() == true }
    private val isPasswordValid2 = password2.map { it?.isNotEmpty() == true && it == password.value }

    val isFormValid = MediatorLiveData<Boolean>().apply {
        addSource(isEmailValid) { value = validateForm() }
        addSource(isPasswordValid) { value = validateForm() }
        addSource(isPasswordValid2) { value = validateForm() }
    }

    private fun validateForm(): Boolean {
        return isEmailValid.value == true && isPasswordValid.value == true && isPasswordValid2.value == true
    }

    fun registerEmailPassword(email: String, password: String, level: String) {
        _showProgressDialog.value = true
        viewModelScope.launch {
            val (isSuccessful, message) = repositoryAuth.registerEmailPassword(email, password)
            if (isSuccessful) {
                val user = repositoryAuth.auth.currentUser
                if (user != null && repositoryDatabase.addUserToDatabase(user.uid, email, level, "")) {
                    _showProgressDialog.value = false
                    _showToast.value = context.getString(R.string.success_create_account)
                    if (level == "Customer"){
                        Intent(context, LoginActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(it)
                            repositoryAuth.auth.signOut()
                        }
                    } else {
                        Intent(context, RegisterBarberActivity::class.java).also {
                            context.startActivity(it)
                            repositoryAuth.auth.signOut()
                        }
                    }
                } else {
                    _showProgressDialog.value = false
                    _showToast.value = context.getString(R.string.fail_save_data)
                }
            } else {
                _showProgressDialog.value = false
                _showToast.value = context.getString(R.string.fail_create_account, message)
            }
        }
    }


    fun loginWithGoogle(result: GoogleSignInAccount, level: String) {
        viewModelScope.launch {
            val credential = repositoryAuth.loginWithGoogle(result)
            moveActivity(credential, level)
        }
    }

    fun loginWithFacebook(token: AccessToken, level: String) {
        viewModelScope.launch {
            val credential = repositoryAuth.loginWithFacebook(token, context.getString(R.string.failed_to_link_accounts))
            moveActivity(credential, level)
        }
    }

    private suspend fun moveActivity(credential: Pair<Boolean, String>, level: String) {
        if (credential.first) {
            val user = repositoryAuth.auth.currentUser
            if (user != null) {
                val isNewUser = !repositoryDatabase.checkUserExists(user.uid)
                if (isNewUser) {
                    if (repositoryDatabase.addUserToDatabase(user.uid, user.displayName.toString(), level, user.photoUrl.toString())) {
                        _showProgressDialog.value = false
                        _showToast.value = context.getString(R.string.register_success)
                        if (level == "Customer"){
                            Intent(context, MainActivity::class.java).also { intent ->
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        } else {
                            Intent(context, RegisterBarberActivity::class.java).also { intent ->
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        }
                    } else {
                        _showToast.value = context.getString(R.string.fail_save_data)
                        _showProgressDialog.value = false
                    }
                } else {
                    _showToast.value = context.getString(R.string.already_have_an_account)
                    _showProgressDialog.value = false
                }
            } else {
                _showToast.value = context.getString(R.string.fail_connect_data)
            }
        } else {
            _showToast.value = credential.second
        }
    }
}