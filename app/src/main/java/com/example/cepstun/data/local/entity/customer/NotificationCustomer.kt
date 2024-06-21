package com.example.cepstun.data.local.entity.customer

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class NotificationCustomer (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    var nameBarber: String = "",

    var modelBarber: String = "",

    var addOnBarber: String? = null,

    var status: String = "",

    var message: String? = null,

    var date: String = "",
): Parcelable
