package com.example.cepstun.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class BarberData (
    val id: String,
    val barberId: String,
    val name: String,
    val logo: String,
    val rate: Double?,
    val lat: Double,
    val lon: Double,
    val location: String,
    val image: List<Image>,
    val model: List<Model>,
    val rating: List<Rating>
)

data class Image (
    val id: String,
    val picture: String
)

@Parcelize
data class Model (
    val id: String,
    val name: String,
    val image: String,
    val price: Double,
): Parcelable

data class Rating (
    val id: String,
    val name: String,
    val review: String,
    val ratingScore: Double,
    val date: String,
    val model: String,
    val addOns: String,
    val image: String
)
