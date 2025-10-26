package com.parkingSystem.parkingSystem.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkingSystem.parkingSystem.viewmodel.StaffViewModel
import com.parkingSystem.parkingSystem.viewmodel.StaffUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHistoryScreen(staffViewModel: StaffViewModel) {
    val allBookings by staffViewModel.allBookings.collectAsState()
    val uiState by staffViewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        staffViewModel.loadAllBookings()
    }

    val filteredBookings = when (selectedFilter) {
        "Completed" -> allBookings.filter { it.status == "completed" }
        "Cancelled" -> allBookings.filter { it.status == "cancelled" }
        "Confirmed" -> allBookings.filter { it.status == "confirmed" }
        else -> allBookings
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Bookings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { staffViewModel.loadAllBookings() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "All",
                onClick = { selectedFilter = "All" },
                label = { Text("All (${allBookings.size})") }
            )
            FilterChip(
                selected = selectedFilter == "Completed",
                onClick = { selectedFilter = "Completed" },
                label = { Text("Completed") }
            )
            FilterChip(
                selected = selectedFilter == "Cancelled",
                onClick = { selectedFilter = "Cancelled" },
                label = { Text("Cancelled") }
            )
            FilterChip(
                selected = selectedFilter == "Confirmed",
                onClick = { selectedFilter = "Confirmed" },
                label = { Text("Confirmed") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bookings List
        when {
            uiState is StaffUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            filteredBookings.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "No bookings found",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBookings) { booking ->
                        BookingCard(
                            booking = booking,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}