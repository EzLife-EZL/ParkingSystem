package com.parkingSystem.parkingSystem.api

import com.google.gson.annotations.SerializedName

data class FirebaseTimestamp(
    @SerializedName("_seconds")
    val seconds: Long = 0,

    @SerializedName("_nanoseconds")
    val nanoseconds: Long = 0
)