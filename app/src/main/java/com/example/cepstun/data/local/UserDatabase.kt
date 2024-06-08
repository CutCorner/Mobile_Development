package com.example.cepstun.data.local

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
data class UserDatabase (
    val fname: String? = null,
    val level: String? = null,
    val photo: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val open: Boolean = false,
    val onGoing: onGoingCustomer? = null,
)


data class onGoingCustomer (
    val idBarber: String? = null,
    val logoBarber: String? = null,
)

data class Booking(
    val userId: String,
    val name: String,
    val model: String,
    val addon: String,
    val price: Double,
    val proses: String
)