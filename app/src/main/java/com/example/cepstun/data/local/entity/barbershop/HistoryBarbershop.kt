package com.example.cepstun.data.local.entity.barbershop

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class HistoryBarbershop(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    var userId: String = "",

    var nameCustomer: String = "",

    var positionCustomer: Int = 0,

    var modelCustomer: String = "",

    var addOnCustomer: String? = null,

    var status: String = "",

    var price: Int = 0,

    var reason: String? = null
) : Parcelable