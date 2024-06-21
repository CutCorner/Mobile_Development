package com.example.cepstun.data.local.entity.customer

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(indices = [Index(value = ["idBarber"], unique = true)])
data class FavoriteCustomer (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "idBarber")
    var idBarber: String = "",

    var logoBarber: String = "",

    var nameBarber: String = "",

    var ratingBarber: String = ""
): Parcelable
