package com.example.cepstun.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class HistoryCustomer(
    @PrimaryKey(autoGenerate = true)
    var idBarber: Int,

    var photoBarber: String = "",

    var nameBarber: String = "",

    var modelBarber: String = "",

    var addOnBarber: String? = null,

    var priceBarber: Int = 0
) : Parcelable
