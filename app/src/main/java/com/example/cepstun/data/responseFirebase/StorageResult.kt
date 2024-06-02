package com.example.cepstun.data.responseFirebase

import android.net.Uri

sealed class StorageResult {
    data class Success(val uri: Uri) : StorageResult()
    data class Error(val exception: Exception) : StorageResult()
}