package com.parkingSystem.parkingSystem.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.responsemodel.*
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import com.parkingSystem.parkingSystem.api.StaffService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
sealed class StaffUiState {
    object Loading : StaffUiState()
    object Success : StaffUiState()
    data class Error(val message: String) : StaffUiState()
}

class StaffViewModel : ViewModel() {

    private val staffService: StaffService by lazy {
        RetrofitInstance.staffService
    }

    // UI State
    private val _uiState = MutableStateFlow<StaffUiState>(StaffUiState.Success)
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    // Today Stats
    private val _todayStats = MutableStateFlow(TodayStats())
    val todayStats: StateFlow<TodayStats> = _todayStats.asStateFlow()

    // All Bookings
    private val _allBookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val allBookings: StateFlow<List<BookingDto>> = _allBookings.asStateFlow()

    // Today Bookings
    private val _todayBookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val todayBookings: StateFlow<List<BookingDto>> = _todayBookings.asStateFlow()

    // Available Slots
    private val _availableSlots = MutableStateFlow<List<AvailableSlot>>(emptyList())
    val availableSlots: StateFlow<List<AvailableSlot>> = _availableSlots.asStateFlow()

    // Pending Reservations
    private val _pendingReservations = MutableStateFlow<List<PendingReservation>>(emptyList())
    val pendingReservations: StateFlow<List<PendingReservation>> = _pendingReservations.asStateFlow()

    init {
        loadTodayStats()
        loadTodayBookings()
    }

    // Get Today Stats
    fun loadTodayStats() {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.getTodayStats()
                if (response.isSuccessful && response.body() != null) {
                    _todayStats.value = response.body()!!
                    _uiState.value = StaffUiState.Success
                    Log.d("StaffViewModel", "Today stats loaded: ${response.body()}")
                } else {
                    _uiState.value = StaffUiState.Error("Failed to load stats")
                    Log.e("StaffViewModel", "Error loading stats: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _uiState.value = StaffUiState.Error(e.message ?: "Unknown error")
                Log.e("StaffViewModel", "Exception loading stats", e)
            }
        }
    }

    // Get All Bookings
    fun loadAllBookings() {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.getAllBookings()
                if (response.isSuccessful && response.body() != null) {
                    _allBookings.value = response.body()!!
                    _uiState.value = StaffUiState.Success
                    Log.d("StaffViewModel", "All bookings loaded: ${response.body()?.size} items")
                } else {
                    _uiState.value = StaffUiState.Error("Failed to load bookings")
                    Log.e("StaffViewModel", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _uiState.value = StaffUiState.Error(e.message ?: "Unknown error")
                Log.e("StaffViewModel", "Exception loading bookings", e)
            }
        }
    }

    // Get Today Bookings
    fun loadTodayBookings() {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.getTodayBookings()
                if (response.isSuccessful && response.body() != null) {
                    _todayBookings.value = response.body()!!
                    _uiState.value = StaffUiState.Success
                    Log.d("StaffViewModel", "Today bookings loaded: ${response.body()?.size} items")
                } else {
                    _uiState.value = StaffUiState.Error("Failed to load today bookings")
                    Log.e("StaffViewModel", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _uiState.value = StaffUiState.Error(e.message ?: "Unknown error")
                Log.e("StaffViewModel", "Exception loading today bookings", e)
            }
        }
    }

    // Get Bookings by Status
    fun loadBookingsByStatus(status: String) {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.getBookingsByStatus(status)
                if (response.isSuccessful && response.body() != null) {
                    _allBookings.value = response.body()!!
                    _uiState.value = StaffUiState.Success
                    Log.d("StaffViewModel", "Bookings by status loaded: ${response.body()?.size} items")
                } else {
                    _uiState.value = StaffUiState.Error("Failed to load bookings by status")
                    Log.e("StaffViewModel", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _uiState.value = StaffUiState.Error(e.message ?: "Unknown error")
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }

    // Get Available Slots
    fun loadAvailableSlots(parkId: String) {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.getAvailableSlots(parkId)
                if (response.isSuccessful && response.body() != null) {
                    _availableSlots.value = response.body()!!
                    _uiState.value = StaffUiState.Success
                    Log.d("StaffViewModel", "Available slots loaded: ${response.body()?.size} slots")
                } else {
                    _uiState.value = StaffUiState.Error("Failed to load available slots")
                    Log.e("StaffViewModel", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _uiState.value = StaffUiState.Error(e.message ?: "Unknown error")
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }

    // Create Walk-in Booking
    fun createWalkInBooking(
        bookingData: WalkInBookingRequest,
        onSuccess: (WalkInBookingResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.createWalkInBooking(bookingData)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = StaffUiState.Success
                    onSuccess(response.body()!!)
                    loadTodayStats() // Refresh stats
                    loadTodayBookings() // Refresh bookings
                    Log.d("StaffViewModel", "Walk-in booking created successfully")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to create booking"
                    _uiState.value = StaffUiState.Error(errorMsg)
                    onError(errorMsg)
                    Log.e("StaffViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                _uiState.value = StaffUiState.Error(errorMsg)
                onError(errorMsg)
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }

    // Check-in Booking
    fun checkInBooking(
        bookingId: String,
        onSuccess: (CheckInResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.checkInBooking(bookingId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = StaffUiState.Success
                    onSuccess(response.body()!!)
                    loadTodayStats() // Refresh stats
                    loadTodayBookings() // Refresh bookings
                    Log.d("StaffViewModel", "Check-in successful")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Check-in failed"
                    _uiState.value = StaffUiState.Error(errorMsg)
                    onError(errorMsg)
                    Log.e("StaffViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                _uiState.value = StaffUiState.Error(errorMsg)
                onError(errorMsg)
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }

    // Update Payment Status
    fun updatePaymentStatus(
        bookingId: String,
        statusPayment: String,
        onSuccess: (PaymentUpdateResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.updatePaymentStatus(
                    bookingId,
                    mapOf("statusPayment" to statusPayment)
                )
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = StaffUiState.Success
                    onSuccess(response.body()!!)
                    loadTodayStats() // Refresh stats
                    loadTodayBookings() // Refresh bookings
                    Log.d("StaffViewModel", "Payment status updated")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Update failed"
                    _uiState.value = StaffUiState.Error(errorMsg)
                    onError(errorMsg)
                    Log.e("StaffViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                _uiState.value = StaffUiState.Error(errorMsg)
                onError(errorMsg)
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }

    // Update Slot Status
    fun updateSlotStatus(
        parkId: String,
        slotId: String,
        isBooked: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.updateSlotStatus(
                    parkId,
                    slotId,
                    mapOf("isBooked" to isBooked)
                )
                if (response.isSuccessful) {
                    _uiState.value = StaffUiState.Success
                    onSuccess()
                    Log.d("StaffViewModel", "Slot status updated")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Update failed"
                    _uiState.value = StaffUiState.Error(errorMsg)
                    onError(errorMsg)
                    Log.e("StaffViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                _uiState.value = StaffUiState.Error(errorMsg)
                onError(errorMsg)
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }

    // Get Pending Reservations
    fun loadPendingReservations() {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.getPendingReservations()
                if (response.isSuccessful && response.body() != null) {
                    // Handle both Object and Array responses
                    val body = response.body()
                    val reservationsList = when (body) {
                        is List<*> -> body.filterIsInstance<PendingReservation>()
                        is Map<*, *> -> body.values.filterIsInstance<PendingReservation>()
                        else -> emptyList()
                    }
                    _pendingReservations.value = reservationsList
                    _uiState.value = StaffUiState.Success
                    Log.d("StaffViewModel", "Pending reservations loaded: ${reservationsList.size}")
                } else {
                    _uiState.value = StaffUiState.Error("Failed to load reservations")
                    Log.e("StaffViewModel", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _uiState.value = StaffUiState.Error(e.message ?: "Unknown error")
                Log.e("StaffViewModel", "Exception loading pending reservations", e)
            }
        }
    }

    // Confirm Reservation
    fun confirmReservation(
        reservationId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = StaffUiState.Loading
            try {
                val response = staffService.confirmReservation(reservationId)
                if (response.isSuccessful) {
                    _uiState.value = StaffUiState.Success
                    onSuccess()
                    loadPendingReservations() // Refresh list
                    Log.d("StaffViewModel", "Reservation confirmed")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Confirmation failed"
                    _uiState.value = StaffUiState.Error(errorMsg)
                    onError(errorMsg)
                    Log.e("StaffViewModel", "Error: $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                _uiState.value = StaffUiState.Error(errorMsg)
                onError(errorMsg)
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }

    // Get Total Booked Slots
    fun getTotalBookedSlots(
        parkId: String,
        onResult: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = staffService.getTotalBookedSlots(parkId)
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!)
                    Log.d("StaffViewModel", "Total booked slots: ${response.body()}")
                } else {
                    onError(response.errorBody()?.string() ?: "Failed to get booked slots")
                    Log.e("StaffViewModel", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
                Log.e("StaffViewModel", "Exception", e)
            }
        }
    }
}