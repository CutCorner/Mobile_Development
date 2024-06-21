package com.example.cepstun.data.local

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties
import java.util.UUID

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
    val onGoing: OnGoingCustomer? = null,
)


data class OnGoingCustomer (
    val idBarber: String? = null,
    val logoBarber: String? = null,
)

data class Booking(
    val idOrder: String,
    val userId: String,
    val name: String,
    val model: String,
    val addon: String,
    val price: Double,
    val proses: String
)

data class AddAddOn (
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double
)

data class AddModel(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri,
    val name: String,
    val price: Double
)

data class ListOrder(
    val position: Int,
    val userId: String,
    val name: String,
    val model: String,
    val addon: String,
    val price: Double,
    val proses: String
)

data class FeedbackData(
    val idBarber: String,
    val nameBarber: String,
    val logoBarber: String,
    val modelBarber: String,
    val addOnBarber: String,
    val priceBarber: Int,
    val status: String,
    val message: String
)

data class AddOn(
    val name: String,
    val price: Int,
    var isSelected: Boolean = false
)

data class Queue (
    val barberID: String,
    val yourQueue: String,
)

data class QueueData(
    val addOn: String? = null,
    val idOrder: String? = null,
    val model: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val proses: String? = null,
    val userId: String? = null
)
