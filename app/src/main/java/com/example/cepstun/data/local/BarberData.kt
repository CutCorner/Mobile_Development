package com.example.cepstun.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class BarberData (
    val id: String,
    val name: String,
    val logo: String,
    val rate: Double,
    val lat: Double,
    val lon: Double,
    val location: String
)

data class Image (
    val id: String,
    val picture: List<String>
)

data class Model (
    val id: String,
    val name: List<String>,
    val image: List<String>,
    val price: List<Double>,
)

data class Rating (
    val id: String,
    val name: List<String>,
    val review: List<String>,
    val ratingScore: List<Double>,
    val date: List<String>,
    val model: List<String>,
    val addOns: List<String>,
    val image: List<String>
)
