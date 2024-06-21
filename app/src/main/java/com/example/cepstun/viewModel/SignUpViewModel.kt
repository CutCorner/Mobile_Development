package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.cepstun.R
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.ui.activity.LoginActivity
import com.example.cepstun.ui.activity.MainActivity
import com.example.cepstun.ui.activity.barbershop.RegisterBarberActivity
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    private val barberApi: RepositoryBarberApi,
    application: Application
) : AndroidViewModel(application){

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: MutableLiveData<String> = _message

//    private val _moveLogin = MutableLiveData<Boolean>()
//    val moveLogin: MutableLiveData<Boolean> = _moveLogin
//
//    private val _moveRegist = MutableLiveData<Boolean>()
//    val moveRegist: MutableLiveData<Boolean> = _moveRegist

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
        _isLoading.value = true
        viewModelScope.launch {
            val credential = repositoryAuth.registerEmailPassword(email, password)
            if (credential.first) {
                val user = repositoryAuth.auth.currentUser
                if (user != null && repositoryDatabase.uploadToDatabase(level)) {
                    _message.value = context.getString(R.string.success_create_account)
                    if (level == "Customer"){
                        _isLoading.value = false
                        repositoryAuth.auth.signOut()
                        Intent(context, LoginActivity::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    } else {
                        val uid = user.uid
                        val name = user.displayName
                        try {
                            val response = barberApi.registerAccountBarber(name ?: "NewBarber", uid)
                            Log.d("Response", response.toString())
                            _message.value = response.message!!
                            _isLoading.value = false
                            Intent(context, RegisterBarberActivity::class.java).also { intent ->
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        } catch (e: Exception){
                            _isLoading.value = false
                            _message.value = e.message
                            Log.e("Error", "Error when calling registerAccountBarber", e)
                        }
                    }
                } else {
                    _isLoading.value = false
                    _message.value = context.getString(R.string.fail_save_data)
                }
            } else {
                _isLoading.value = false
                _message.value = context.getString(R.string.fail_create_account, credential.second)
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
                    if (repositoryDatabase.uploadToDatabase(level)) {
                        _isLoading.value = false
                        _message.value = context.getString(R.string.register_success)
                        if (level == "Customer"){
                            Intent(context, MainActivity::class.java).also { intent ->
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        } else {
                            val uid = user.uid
                            val name = user.displayName
                            try {
                                val response = barberApi.registerAccountBarber(name ?: "NewBarber", uid)
                                Log.d("Response", response.toString())
                                _message.value = response.message!!
                                _isLoading.value = false
                                Intent(context, RegisterBarberActivity::class.java).also { intent ->
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                }
                            } catch (e: Exception){
                                _isLoading.value = false
                                _message.value = e.message
                                Log.e("Error", "Error when calling registerAccountBarber", e)
                            }
                        }
                    } else {
                        _message.value = context.getString(R.string.fail_save_data)
                        _isLoading.value = false
                    }
                } else {
                    _message.value = context.getString(R.string.already_have_an_account)
                    _isLoading.value = false
                }
            } else {
                _message.value = context.getString(R.string.fail_connect_data)
            }
        } else {
            _message.value = credential.second
        }
    }
}