package com.example.cepstun.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryBarberApi
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryFavorite
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositoryNotification
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.RepositoryStorage
import com.example.cepstun.data.local.room.barbershop.BarbershopDatabase
import com.example.cepstun.data.local.room.customer.CustomerDatabase
import com.example.cepstun.data.remote.retrofit.barber.ApiConfigBarber
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import com.google.maps.internal.ApiConfig
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "prefs_settings")
object Injection {

    fun provideRepositoryAuth(): RepositoryAuth = RepositoryAuth.getInstance(Firebase.auth)

    fun provideRepositoryDatabase(): RepositoryDatabase = RepositoryDatabase.getInstance(Firebase.database)

    fun provideRepositoryStorage(): RepositoryStorage = RepositoryStorage.getInstance(Firebase.storage)

    fun provideRepositoryHistory(context: Context): RepositoryHistory {
        val database = CustomerDatabase.getInstance(context)
        val histCus = database.historyCustomerDao()
        val database2 = BarbershopDatabase.getInstance(context)
        val histBar = database2.historyBarbershopDao()
        return RepositoryHistory.getInstance(histCus, histBar)
    }

    fun provideRepositoryFavorite(context: Context): RepositoryFavorite {
        val database = CustomerDatabase.getInstance(context)
        val favCus = database.favoriteCustomerDao()
        return RepositoryFavorite.getInstance(favCus)
    }

    fun provideRepositoryNotification(context: Context): RepositoryNotification {
        val database = CustomerDatabase.getInstance(context)
        val notificationCus = database.notificationCustomerDao()
        return RepositoryNotification.getInstance(notificationCus)
    }

    fun providePreference(context: Context): RepositorySharedPreference {
        val dataStore = context.dataStore
        return RepositorySharedPreference.getInstance(dataStore)
    }

//    fun provideRepositoryBarberApi(context: Context): RepositoryBarberApi {
//        val userPreference = provideUserPreference(context)
//        val user = runBlocking { userPreference.getUser().first() }
//        val apiService = ApiConfigBarber.getApiService()
//        return RepositoryBarberApi.getInstance(apiService)
//    }

    fun provideRepositoryBarberApi(): RepositoryBarberApi {
        val apiService = ApiConfigBarber.getApiService()
        return RepositoryBarberApi.getInstance(apiService)
    }

}