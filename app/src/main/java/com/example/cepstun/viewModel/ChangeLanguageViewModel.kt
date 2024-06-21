package com.example.cepstun.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.cepstun.data.RepositorySharedPreference

class ChangeLanguageViewModel (
    private val setting: RepositorySharedPreference,
    application: Application
): AndroidViewModel(application) {

    val languageSetting: LiveData<String?> = setting.getLanguageSetting().asLiveData()

    suspend fun setLanguageSetting(language: String) {
        setting.saveLanguageSetting(language)
    }

}