package com.example.cepstun.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.di.Injection
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ViewModelFactory private constructor(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(repositoryAuth) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repositoryAuth, repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repositoryAuth, repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(ChooseUserViewModel::class.java) -> {
                ChooseUserViewModel(repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(AIRecomendationViewModel::class.java) -> {
                AIRecomendationViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name + "Check ViewModelFactory")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun clearInstance(){
            RepositoryAuth.clearInstance()
            RepositoryDatabase.clearInstance()
            INSTANCE = null
        }

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory = INSTANCE ?: synchronized(this) {
            val repositoryAuth = Injection.provideRepositoryAuth()
            val repositoryDatabase = Injection.provideRepositoryDatabase()
            INSTANCE ?: ViewModelFactory(
//                userPreference,
                repositoryAuth,
                repositoryDatabase,
                context.applicationContext as Application)
        }.also { INSTANCE = it }

    }
}