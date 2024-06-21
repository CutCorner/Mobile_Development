package com.example.cepstun.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cepstun.data.RepositorySharedPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class OnBoardingViewModel(
    private val setting: RepositorySharedPreference,
    application: Application
): AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

//    val themeSetting: LiveData<Boolean> = setting.getThemeSetting().asLiveData()
//    val languageSetting: LiveData<String> = setting.getLanguageSetting().asLiveData()

}