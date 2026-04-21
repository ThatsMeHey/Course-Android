package com.example.coursemobile.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CityDto(
    val name: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable