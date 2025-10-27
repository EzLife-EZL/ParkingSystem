package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.requestmodel.MakeReservationBody
import com.parkingSystem.parkingSystem.requestmodel.ReservationResponse
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import retrofit2.Response
import retrofit2.http.*


typealias JsonMap = Map<String, Any>

interface UserApi {
    @POST("user/make-reservation")
    suspend fun makeReservation(@Body body: MakeReservationBody): Response<ReservationResponse>

    @GET("user/available-slots/{parkId}")
    suspend fun getAvailableSlots(@Path("parkId") parkId: String): Response<List<JsonMap>>

    @GET("user/view-booking-history/{userId}")
    suspend fun getBookingHistory(
        @Path("userId") userId: String
    ): Response<List<BookingDto>>

    @GET("user/booking/details/api/{bookingId}")
    suspend fun getBookingDetails(
        @Path("bookingId") bookingId: String
    ): Response<BookingDto>

    @POST("user/cancel-reservation/{bookingId}")
    suspend fun cancelReservation(
        @Path("bookingId") bookingId: String
    ): Response<Any>
}
