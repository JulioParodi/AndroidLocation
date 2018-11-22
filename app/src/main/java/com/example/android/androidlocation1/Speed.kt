package com.example.android.androidlocation1

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.location.Location

@Entity
data class Speed(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val realSpeed: String,
    val mySpeed: String,
    val currentDate: String,
    val longitude : String,
    val latitude : String
)
