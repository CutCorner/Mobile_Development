package com.example.cepstun.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositoryDatabase
import com.example.cepstun.data.RepositoryHistory
import com.example.cepstun.data.RepositorySharedPreference
import com.example.cepstun.data.RepositoryStorage
import com.example.cepstun.data.local.room.CustomerDatabase
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.storage

val Context.dataStore by preferencesDataStore(name = "prefs_settings")
object Injection {

    fun provideRepositoryAuth(): RepositoryAuth = RepositoryAuth.getInstance(Firebase.auth)

    fun provideRepositoryDatabase(): RepositoryDatabase = RepositoryDatabase.getInstance(Firebase.database)

    fun provideRepositoryStorage(): RepositoryStorage = RepositoryStorage.getInstance(Firebase.storage)

    fun provideRepositoryHistory(context: Context): RepositoryHistory {
        val database = CustomerDatabase.getInstance(context)
        val histCus = database.historyCustomerDao()
        return RepositoryHistory.getInstance(histCus)
    }

    fun providePreference(context: Context): RepositorySharedPreference {
        val dataStore = context.dataStore
        return RepositorySharedPreference.getInstance(dataStore)
    }


}