package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.requestmodel.AdminResponseRequest
import com.parkingSystem.parkingSystem.requestmodel.ReportIssue
import com.parkingSystem.parkingSystem.responsemodel.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @Headers("Content-Type: application/json")
    @POST("report")
    suspend fun createReport(
        @Body body: ReportIssue
    ): Response<Unit>

    @GET("/report")
    suspend fun getAllReports(): List<ReportResponse>

    @PATCH("/report/{id}/response")
    suspend fun sendAdminResponse(
        @Path("id") id: String,
        @Body response: AdminResponseRequest
    ): Response<Void>

    @DELETE("/report/{id}")
    suspend fun deleteReport(@Path("id") id: String): Response<Void>

    @GET("report/user/{userId}")
    suspend fun getMyReports(
        @Path("userId") userId: String
    ): List<ReportResponse>
}
