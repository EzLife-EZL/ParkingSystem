package com.parkingSystem.parkingSystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetNotificationResponse(
    @SerializedName("user") val user: String,
    @SerializedName("content") val content: String,
    @SerializedName("isRead") val isRead: Boolean,
)

data class CreateNotificationResponse(
    @SerializedName("user") val user: String,
    @SerializedName("content") val content: String,
    @SerializedName("isRead") val isRead: Boolean,
)

data class NotificationResponse(
    @SerializedName("id") val id: String,
    @SerializedName("message") val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("isRead") val isRead: Boolean=true,
    @SerializedName("navPath") val navPath: String="",
)
