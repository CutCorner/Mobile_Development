package com.example.cepstun.data


import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cepstun.data.local.Queue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class RepositorySharedPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUserLevel(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_LEVEL_KEY] ?: "null"
        }
    }

    suspend fun saveUserLevel(userLevel: String) {
        dataStore.edit { preferences ->
            preferences[USER_LEVEL_KEY] = userLevel
        }
    }

    suspend fun clearUserLevel() {
        dataStore.edit { preferences ->
            preferences.remove(USER_LEVEL_KEY)
        }
    }


    fun getThemeSetting(): Flow<Boolean?> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY]
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    fun getLanguageSetting(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY]
        }
    }

    suspend fun saveLanguageSetting(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: RepositorySharedPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): RepositorySharedPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositorySharedPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val THEME_KEY = booleanPreferencesKey("theme_setting")
        private val LANGUAGE_KEY = stringPreferencesKey("language_setting")
        private val USER_LEVEL_KEY = stringPreferencesKey("user_level")
    }
}