package com.example.cepstun.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarberData (
    val id: String,
    val name: String,
    val image: String,
    val logo: String,
    val rate: Double,
    val lat: Double,
    val lon: Double,
    val location: String
): Parcelable