package com.parkingSystem.parkingSystem.requestmodel

data class ReportIssue(
    val bookingId: String,
    val userId: String,
    val slotId: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val status: String = "opened"
)