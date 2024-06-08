package com.example.cepstun.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.RepositoryStorage
import com.example.cepstun.data.di.Injection

class ViewModelFactory private constructor(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    private val repositoryStorage: RepositoryStorage,
    private val repositoryHistory: RepositoryHistory,
    private val repositoryPreference: RepositorySharedPreference,
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
            modelClass.isAssignableFrom(MenuHomeViewModel::class.java) -> {
                MenuHomeViewModel(application) as T
            }
            modelClass.isAssignableFrom(MenuProfileViewModel::class.java) -> {
                MenuProfileViewModel(repositoryAuth, repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repositoryAuth, repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(PersonalDataViewModel::class.java) -> {
                PersonalDataViewModel(repositoryAuth, repositoryDatabase, repositoryStorage, application) as T
            }
            modelClass.isAssignableFrom(OrderPagerViewModel::class.java) -> {
                OrderPagerViewModel(repositoryHistory, repositoryDatabase, repositoryAuth, application) as T
            }
            modelClass.isAssignableFrom(CheckoutViewModel::class.java) -> {
                CheckoutViewModel(repositoryAuth, repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(BarbershopViewModel::class.java) -> {
                BarbershopViewModel(repositoryAuth, repositoryDatabase) as T
            }
            modelClass.isAssignableFrom(BarberLocationViewModel::class.java) -> {
                BarberLocationViewModel(repositoryHistory, repositoryDatabase, application) as T
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
            val repositoryStorage = Injection.provideRepositoryStorage()
            val history = Injection.provideRepositoryHistory(context)
            val preference = Injection.providePreference(context)
            INSTANCE ?: ViewModelFactory(
                repositoryAuth,
                repositoryDatabase,
                repositoryStorage,
                history,
                preference,
                context.applicationContext as Application)
        }.also { INSTANCE = it }

    }
}