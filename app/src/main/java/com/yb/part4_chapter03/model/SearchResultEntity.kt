package com.yb.part4_chapter03.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultEntity(
    val address: String,
    val detailAddress: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable
