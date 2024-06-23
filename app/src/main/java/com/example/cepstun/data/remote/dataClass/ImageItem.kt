package com.example.cepstun.data.remote.dataClass

import android.net.Uri
import java.io.File

sealed class ImageItem {
    data class UriImage(val uri: Uri) : ImageItem()
    data class UrlImage(val url: String, val file: File? = null) : ImageItem()
}