package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositoryAuth
import com.example.cepstun.data.RepositorySharedPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class SplashScreenViewModel (
    private val setting: RepositorySharedPreference,
    private val repositoryAuth: RepositoryAuth,
    application: Application
): AndroidViewModel(application) {

    private val _themeSetting = MutableLiveData<Boolean?>()
    val themeSetting: LiveData<Boolean?> = _themeSetting

    private val _languageSetting = MutableLiveData<String?>()
    val languageSetting: LiveData<String?> = _languageSetting

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    fun init() {
//        viewModelScope.launch {
//            if (themeSetting.value == null) {
//                Log.d("TAG", "setThemeSetting Kosong")
//                val isDarkModeActive = getDefaultThemeSetting()
//                setThemeSetting(isDarkModeActive)
//            }
//
//            if (languageSetting.value == null) {
//                Log.d("TAG", "setLanguageSetting Kosong")
//                val language = getDefaultLanguageSetting()
//                setLanguageSetting(language)
//            }
//        }
//        viewModelScope.launch {
//            setting.getThemeSetting().collect { themeSettingValue ->
//                if (themeSettingValue == null) {
//                    val defaultThemeSetting = getDefaultThemeSetting()
//                    setThemeSetting(defaultThemeSetting)
//                    _themeSetting.value = defaultThemeSetting
//                } else {
//                    _themeSetting.value = themeSettingValue
//                }
//            }
//
//            setting.getLanguageSetting().collect { languageSettingValue ->
//                if (languageSettingValue == null) {
//                    val defaultLanguageSetting = getDefaultLanguageSetting()
//                    setLanguageSetting(defaultLanguageSetting)
//                    _languageSetting.value = defaultLanguageSetting
//                } else {
//                    _languageSetting.value = languageSettingValue
//                }
//            }
//        }
        viewModelScope.launch {
            val themeSettingValue = setting.getThemeSetting().first()
            if (themeSettingValue == null) {
                val defaultThemeSetting = getDefaultThemeSetting()
                setThemeSetting(defaultThemeSetting)
                _themeSetting.value = defaultThemeSetting
            } else {
                _themeSetting.value = themeSettingValue
            }
        }

        viewModelScope.launch {
            val languageSettingValue = setting.getLanguageSetting().first()
            if (languageSettingValue == null) {
                val defaultLanguageSetting = getDefaultLanguageSetting()
                setLanguageSetting(defaultLanguageSetting)
                _languageSetting.value = defaultLanguageSetting
            } else {
                _languageSetting.value = languageSettingValue
            }
        }
    }

    private suspend fun setThemeSetting(isDarkModeActive: Boolean) {
        Log.d("theme", "Set Theme : $isDarkModeActive")
        setting.saveThemeSetting(isDarkModeActive)
    }

    private suspend fun setLanguageSetting(language: String) {
        Log.d("language", "Set Language : $language")
        setting.saveLanguageSetting(language)
    }

    private fun getDefaultThemeSetting(): Boolean {
        Log.d("theme", "Ambil Default Theme")
        return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun getDefaultLanguageSetting(): String {
        Log.d("language", "Ambil Default Language")
        return Locale.getDefault().country
    }

    fun cekUserLogin(): Boolean {
        return repositoryAuth.currentUser != null
    }
}