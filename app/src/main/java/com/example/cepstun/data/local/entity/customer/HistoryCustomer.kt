package com.example.cepstun.data.local.entity.customer

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class HistoryCustomer(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    var idBarber: String = "",

    var logoBarber: String = "",

    var nameBarber: String = "",

    var modelBarber: String = "",

    var addOnBarber: String? = null,

    var status: String = "",

    var priceBarber: Int = 0,

    var message: String? = null,
) : Parcelable
