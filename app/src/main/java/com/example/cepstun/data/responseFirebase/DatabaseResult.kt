package com.example.cepstun.data.responseFirebase

sealed class DatabaseUpdateResult {
    object Success : DatabaseUpdateResult()
    data class Error(val exception: Exception) : DatabaseUpdateResult()
}