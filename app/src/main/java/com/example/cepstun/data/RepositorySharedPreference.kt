package com.example.cepstun.data


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.cepstun.data.local.Queue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositorySharedPreference private constructor(private val dataStore: DataStore<Preferences>) {

    val queueSetting: Flow<Queue>
        get() = dataStore.data.map { preferences ->
            Queue(
                barberID = preferences[BARBER_ID] ?: "",
                yourQueue = preferences[YOUR_QUEUE] ?: ""
            )
        }

//    fun getQueue(): Flow<Queue>{
//        Log.d("Repository", "pemanggilan getQueue")
//        return dataStore.data.map { preferences ->
//            Queue(
//                preferences[BARBER_ID]?: "",
//                preferences[YOUR_QUEUE] ?: "",
//            )
//        }
//    }
    suspend fun deleteQueueSetting() {
        dataStore.edit { preferences ->
            preferences.remove(BARBER_ID)
            preferences.remove(YOUR_QUEUE)
        }
    }

    suspend fun saveQueueSetting(barberId: String, yourQueue: String) {
        Log.d("Repository", "proses saveQueue: $barberId, $yourQueue")
        dataStore.edit { preferences ->
            preferences[BARBER_ID] = barberId
            preferences[YOUR_QUEUE] = yourQueue
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

        private val BARBER_ID = stringPreferencesKey("barberId")
        private val YOUR_QUEUE = stringPreferencesKey("yourQueue")
    }
}