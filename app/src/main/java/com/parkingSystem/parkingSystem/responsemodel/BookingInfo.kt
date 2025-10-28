package com.parkingSystem.parkingSystem.responsemodel

data class BookingInfo(
    val id: String,
    val numberPlate: String,
    val parkId: String,
    val parkName: String = "", // Cần lấy từ parkId
    val slotName: String,
    val pos_X: String,
    val pos_Y: String,
    val checkInTime: String?,
    val checkOutTime: String?,
    val status: String,
    val statusPayment: String,
    val slotStatus: String,
    val paymentMethod: String,
    val price: Double,
    val userId: String,
    val createdAt: String
)