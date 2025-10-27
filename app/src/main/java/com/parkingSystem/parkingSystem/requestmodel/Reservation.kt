package com.parkingSystem.parkingSystem.requestmodel

data class MakeReservationBody(
    val parkId: String,
    val slotId: String,
    val userId: String,
    val price: Double,
    val startTime: String,
    val endTime: String,
    val numberPlate: String? = null,
    val paymentMethod: String? = "cash",
    val statusPayment: String? = "unpaid"
)

data class ReservationResponse(
    val message: String?,
    val reservation: Map<String, Any>?
)
