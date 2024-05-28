package com.example.cepstun.data.local

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class UserDatabase (
    val fname: String? = null,
    val level: String? = null,
    val photo: String? = null
): Parcelable