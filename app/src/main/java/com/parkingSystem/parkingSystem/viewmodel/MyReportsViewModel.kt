package com.parkingSystem.parkingSystem.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.api.ReportService
import com.parkingSystem.parkingSystem.responsemodel.ReportResponse
import kotlinx.coroutines.launch

class MyReportsViewModel(
    private val api: ReportService,
    private val currentUserId: String
) : ViewModel() {

    var reports by mutableStateOf<List<ReportResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var loadError by mutableStateOf<String?>(null)
        private set

    fun setLoadErrorFromOutside(msg: String) {
        loadError = msg
        isLoading = false
        reports = emptyList()
    }

    fun loadMyReports() {
        viewModelScope.launch {
            isLoading = true
            loadError = null

            try {
                val res = api.getMyReports(currentUserId)

                reports = res.map { dto ->
                    ReportResponse(
                        _id = dto._id,
                        bookingId = dto.bookingId,
                        slotId = dto.slotId,
                        userId = dto.userId,
                        title = dto.title,
                        content = dto.content,
                        status = dto.status,
                        createdAt = dto.createdAt,
                        responseContent = dto.responseContent,
                        responseTime = dto.responseTime
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                loadError = "Failed to load your reports"
            } finally {
                isLoading = false
            }
        }
    }
}
