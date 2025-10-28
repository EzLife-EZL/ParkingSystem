package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.requestmodel.CreateAppointmentRequest
import com.parkingSystem.parkingSystem.requestmodel.UpdateAppointmentRequest
import com.parkingSystem.parkingSystem.responsemodel.AppointmentResponse
import com.parkingSystem.parkingSystem.responsemodel.ParkingOverview
import com.parkingSystem.parkingSystem.responsemodel.RevenueReport
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import com.parkingSystem.parkingSystem.roomDb.data.dao.AppointmentDao
import com.parkingSystem.parkingSystem.roomDb.mapper.isValid
import com.parkingSystem.parkingSystem.roomDb.mapper.toEntity
import com.parkingSystem.parkingSystem.roomDb.mapper.toEntitySafe
import com.parkingSystem.parkingSystem.roomDb.mapper.toResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class AdminViewModel : ViewModel() {

    // Parking Overview State
    private val _parkingOverview = MutableStateFlow<ParkingOverview?>(null)
    val parkingOverview: StateFlow<ParkingOverview?> = _parkingOverview.asStateFlow()

    private val _isParkingOverviewLoading = MutableStateFlow(false)
    val isParkingOverviewLoading: StateFlow<Boolean> = _isParkingOverviewLoading.asStateFlow()

    private val _parkingOverviewError = MutableStateFlow<String?>(null)
    val parkingOverviewError: StateFlow<String?> = _parkingOverviewError.asStateFlow()

    // Revenue Report State
    private val _revenueReport = MutableStateFlow<RevenueReport?>(null)
    val revenueReport: StateFlow<RevenueReport?> = _revenueReport.asStateFlow()

    private val _isRevenueLoading = MutableStateFlow(false)
    val isRevenueLoading: StateFlow<Boolean> = _isRevenueLoading.asStateFlow()

    private val _revenueError = MutableStateFlow<String?>(null)
    val revenueError: StateFlow<String?> = _revenueError.asStateFlow()

    private val _selectedFilter = MutableStateFlow("month")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Initialize data on ViewModel creation
    init {
        loadParkingOverview()
        loadRevenueReport("month")
    }

    /**
     * Load parking overview data
     */
    fun loadParkingOverview() {
        viewModelScope.launch {
            _isParkingOverviewLoading.value = true
            _parkingOverviewError.value = null

            try {
                val response = RetrofitInstance.parking.getParkingOverview()

                Log.d("AdminViewModel", "Overview Response code: ${response.code()}")
                Log.d("AdminViewModel", "Overview Response body: ${response.body()}")

                if (response.isSuccessful) {
                    _parkingOverview.value = response.body()
                    Log.d("AdminViewModel", "Overview data loaded: ${_parkingOverview.value}")
                } else {
                    _parkingOverviewError.value = "Error: ${response.code()}"
                    Log.e("AdminViewModel", "Overview API Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _parkingOverviewError.value = e.message ?: "Unknown error occurred"
                Log.e("AdminViewModel", "Overview Exception: ${e.message}", e)
            } finally {
                _isParkingOverviewLoading.value = false
            }
        }
    }

    /**
     * Load revenue report based on filter
     * @param filter: "day", "week", "month", or "year"
     */
    fun loadRevenueReport(filter: String) {
        viewModelScope.launch {
            _isRevenueLoading.value = true
            _revenueError.value = null
            _selectedFilter.value = filter.lowercase()

            try {
                val response = RetrofitInstance.parking.getRevenueReport(filter.lowercase())

                Log.d("AdminViewModel", "Revenue API Response code: ${response.code()}")
                Log.d("AdminViewModel", "Revenue API Response body: ${response.body()}")

                if (response.isSuccessful) {
                    _revenueReport.value = response.body()
                    Log.d("AdminViewModel", "Revenue data loaded: ${_revenueReport.value}")
                } else {
                    _revenueError.value = "Error: ${response.code()}"
                    Log.e("AdminViewModel", "Revenue API Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _revenueError.value = e.message ?: "Unknown error occurred"
                Log.e("AdminViewModel", "Revenue API Exception: ${e.message}", e)
            } finally {
                _isRevenueLoading.value = false
            }
        }
    }

    /**
     * Update selected filter and reload revenue data
     */
    fun updateFilter(newFilter: String) {
        if (_selectedFilter.value != newFilter.lowercase()) {
            loadRevenueReport(newFilter)
        }
    }

    /**
     * Refresh all data
     */
    fun refreshAllData() {
        loadParkingOverview()
        loadRevenueReport(_selectedFilter.value)
    }

    /**
     * Clear error messages
     */
    fun clearErrors() {
        _parkingOverviewError.value = null
        _revenueError.value = null
    }
}