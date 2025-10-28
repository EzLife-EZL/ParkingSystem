package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.requestmodel.ReportIssue
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BookingDetailViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _booking = MutableStateFlow<BookingDto?>(null)
    val booking: StateFlow<BookingDto?> = _booking

    private val api = RetrofitInstance.userApi

    fun fetchBookingDetail(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val res = api.getBookingDetails(bookingId)
                if (res.isSuccessful) {
                    _booking.value = res.body()
                } else {
                    _error.value = "Không lấy được chi tiết (${res.code()})"
                }
            } catch (e: IOException) {
                _error.value = "Lỗi mạng: ${e.message}"
            } catch (e: HttpException) {
                _error.value = "Lỗi server: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelBooking(bookingId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val res = api.cancelReservation(bookingId)
                if (res.isSuccessful) {
                    onSuccess()
                    fetchBookingDetail(bookingId)
                } else {
                    _error.value = "Huỷ thất bại (${res.code()})"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Huỷ thất bại"
            } finally {
                _isLoading.value = false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun reportIssue(
        bookingId: String,
        slotId: String,
        userId: String,
        title: String,
        content: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val nowIso = java.time.OffsetDateTime.now().toString()

                val body = ReportIssue(
                    bookingId = bookingId,
                    userId = userId,
                    slotId = slotId,
                    title = title,
                    content = content,
                    createdAt = nowIso,
                    status = "opened"
                )

                val res = RetrofitInstance.reportService.createReport(body)

                if (res.isSuccessful) {
                    onSuccess()
                } else {
                    val errText = res.errorBody()?.string()
                    onError(errText ?: "Report failed with ${res.code()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unexpected error")
            }
        }
    }

}
