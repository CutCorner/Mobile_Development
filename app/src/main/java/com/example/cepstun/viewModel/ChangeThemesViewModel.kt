package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.cepstun.data.RepositorySharedPreference

class ChangeThemesViewModel (
    private val setting: RepositorySharedPreference,
    application: Application
): AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val themeSetting: LiveData<Boolean?> = setting.getThemeSetting().asLiveData()

    suspend fun setThemeSetting(active: Boolean){
        setting.saveThemeSetting(active)
    }
}