package com.example.cepstun.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryFavorite
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositoryNotification
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.RepositoryStorage
import com.example.cepstun.data.di.Injection

class ViewModelFactory private constructor(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryDatabase: RepositoryDatabase,
    private val repositoryStorage: RepositoryStorage,
    private val repositoryHistory: RepositoryHistory,
    private val repositoryFavorite: RepositoryFavorite,
    private val repositoryNotification: RepositoryNotification,
    private val repositoryPreference: RepositorySharedPreference,
    private val repositoryBarberApi: RepositoryBarberApi,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(repositoryPreference, repositoryAuth, application) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repositoryAuth, repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repositoryAuth, repositoryDatabase, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(ChooseUserViewModel::class.java) -> {
                ChooseUserViewModel(repositoryDatabase, application) as T
            }
            modelClass.isAssignableFrom(MenuHomeCustomerViewModel::class.java) -> {
                MenuHomeCustomerViewModel(repositoryAuth, repositoryDatabase, repositoryHistory, repositoryNotification, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(MenuProfileCustomerViewModel::class.java) -> {
                MenuProfileCustomerViewModel(repositoryAuth, repositoryDatabase, repositoryHistory, repositoryFavorite, repositoryPreference, repositoryNotification, application) as T
            }
            modelClass.isAssignableFrom(MenuProfileBarberViewModel::class.java) -> {
                MenuProfileBarberViewModel(repositoryAuth, repositoryBarberApi, repositoryHistory, repositoryPreference, application) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repositoryAuth, repositoryDatabase, repositoryPreference, application) as T
            }
            modelClass.isAssignableFrom(PersonalDataViewModel::class.java) -> {
                PersonalDataViewModel(repositoryAuth, repositoryDatabase, repositoryStorage, application) as T
            }
            modelClass.isAssignableFrom(OrderPagerViewModel::class.java) -> {
                OrderPagerViewModel(repositoryHistory, repositoryDatabase, repositoryAuth, application) as T
            }
            modelClass.isAssignableFrom(CheckoutViewModel::class.java) -> {
                CheckoutViewModel(repositoryAuth, repositoryDatabase, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(BarbershopViewModel::class.java) -> {
                BarbershopViewModel(repositoryDatabase, repositoryFavorite, repositoryPreference, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(BarberLocationViewModel::class.java) -> {
                BarberLocationViewModel(repositoryHistory, repositoryDatabase, repositoryPreference, application) as T
            }
            modelClass.isAssignableFrom(AIRecommendationViewModel::class.java) -> {
                AIRecommendationViewModel() as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(repositoryFavorite, application) as T
            }
            modelClass.isAssignableFrom(ChangeLanguageViewModel::class.java) -> {
                ChangeLanguageViewModel(repositoryPreference, application) as T
            }
            modelClass.isAssignableFrom(ChangeThemesViewModel::class.java) -> {
                ChangeThemesViewModel(repositoryPreference, application) as T
            }
            modelClass.isAssignableFrom(OnBoardingViewModel::class.java) -> {
                OnBoardingViewModel(repositoryPreference, application) as T
            }
            modelClass.isAssignableFrom(RegisterBarberViewModel::class.java) -> {
                RegisterBarberViewModel(repositoryAuth, repositoryDatabase, repositoryStorage, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(InputModelViewModel::class.java) -> {
                InputModelViewModel(repositoryAuth, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(InputAddOnViewModel::class.java) -> {
                InputAddOnViewModel(repositoryAuth, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(MenuHomeBarberViewModel::class.java) -> {
                MenuHomeBarberViewModel(repositoryAuth, repositoryDatabase, repositoryHistory, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(ChooseModelViewModel::class.java) -> {
                ChooseModelViewModel(repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(repositoryNotification, application) as T
            }
            modelClass.isAssignableFrom(ChangeModelViewModel::class.java) -> {
                ChangeModelViewModel(repositoryAuth, repositoryBarberApi, application) as T
            }
            modelClass.isAssignableFrom(ChangeAddOnViewModel::class.java) -> {
                ChangeAddOnViewModel(repositoryAuth, repositoryBarberApi, application) as T
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
            val favorite = Injection.provideRepositoryFavorite(context)
            val notification = Injection.provideRepositoryNotification(context)
            val preference = Injection.providePreference(context)
            val repositoryBarberApi = Injection.provideRepositoryBarberApi()
            INSTANCE ?: ViewModelFactory(
                repositoryAuth,
                repositoryDatabase,
                repositoryStorage,
                history,
                favorite,
                notification,
                preference,
                repositoryBarberApi,
                context.applicationContext as Application)
        }.also { INSTANCE = it }

    }
}