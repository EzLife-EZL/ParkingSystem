package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.responsemodel.*
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import retrofit2.Response
import retrofit2.http.*

interface StaffService {

    // Get pending reservations
    @GET("parkingStaff/get-pending-reservations")
    suspend fun getPendingReservations(): Response<Any>

    // Confirm reservation
    @PUT("parkingStaff/confirm-reservation/{reservationId}")
    suspend fun confirmReservation(
        @Path("reservationId") reservationId: String
    ): Response<Any>

    // Update slot status
    @PUT("parkingStaff/update-slot-status/{parkId}/{slotId}")
    suspend fun updateSlotStatus(
        @Path("parkId") parkId: String,
        @Path("slotId") slotId: String,
        @Body isBooked: Map<String, Boolean>
    ): Response<Any>

    // Get all bookings
    @GET("parkingStaff/bookings")
    suspend fun getAllBookings(): Response<List<BookingDto>>

    // Get bookings by status
    @GET("parkingStaff/bookings/status/{status}")
    suspend fun getBookingsByStatus(
        @Path("status") status: String
    ): Response<List<BookingDto>>

    // Get today bookings
    @GET("parkingStaff/bookings/today")
    suspend fun getTodayBookings(): Response<List<BookingDto>>

    // Get total booked slots
    @GET("parkingStaff/total-booked-slots/{parkId}")
    suspend fun getTotalBookedSlots(
        @Path("parkId") parkId: String
    ): Response<Int>

    // Create walk-in booking
    @POST("parkingStaff/bookings/walk-in")
    suspend fun createWalkInBooking(
        @Body bookingData: WalkInBookingRequest
    ): Response<WalkInBookingResponse>

    // Check-in booking
    @PUT("parkingStaff/bookings/{bookingId}/check-in")
    suspend fun checkInBooking(
        @Path("bookingId") bookingId: String
    ): Response<CheckInResponse>

    // Update payment status
    @PUT("parkingStaff/bookings/{bookingId}/payment")
    suspend fun updatePaymentStatus(
        @Path("bookingId") bookingId: String,
        @Body statusPayment: Map<String, String>
    ): Response<PaymentUpdateResponse>

    // Get available slots
    @GET("parkingStaff/parks/{parkId}/available-slots")
    suspend fun getAvailableSlots(
        @Path("parkId") parkId: String
    ): Response<List<AvailableSlot>>

    // Get today stats
    @GET("parkingStaff/stats/today")
    suspend fun getTodayStats(): Response<TodayStats>
}