package com.example.cepstun.viewModel

import androidx.lifecycle.ViewModel
import com.example.cepstun.data.RepositoryAuth

class SplashScreenViewModel (private val repositoryAuth: RepositoryAuth): ViewModel() {

    fun cekUserLogin(): Boolean {
        return repositoryAuth.currentUser != null
    }
}