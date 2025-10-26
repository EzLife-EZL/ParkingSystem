package com.parkingSystem.parkingSystem.responsemodel

data class TodayStats(
    val totalBookings: Int = 0,
    val checkedIn: Int = 0,
    val pending: Int = 0,
    val completed: Int = 0,
    val cancelled: Int = 0,
    val totalRevenue: Double = 0.0,
    val unpaidBookings: Int = 0
)

data class AvailableSlots(
    val id: String,
    val slotName: String,
    val pos_X: Int,
    val pos_Y: Int,
    val isBooked: Boolean = false
)

data class WalkInBookingRequest(
    val parkId: String,
    val slotId: String,
    val numberPlate: String,
    val customerName: String? = null,
    val customerPhone: String? = null,
    val startTime: String,
    val endTime: String,
    val price: Double,
    val type_vehicle: String? = null
)

data class WalkInBookingResponse(
    val message: String,
    val booking: Any
)

data class CheckInResponse(
    val message: String,
    val booking: Any
)

data class PaymentUpdateResponse(
    val message: String,
    val booking: Any
)

data class PendingReservation(
    val id: String,
    val userId: String?,
    val parkId: String?,
    val slotId: String?,
    val status: String?,
    val createdAt: String?
)