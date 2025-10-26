package com.parkingSystem.parkingSystem.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import com.parkingSystem.parkingSystem.viewmodel.StaffViewModel
import com.parkingSystem.parkingSystem.viewmodel.StaffUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingManagementScreen(staffViewModel: StaffViewModel) {
    val todayBookings by staffViewModel.todayBookings.collectAsState()
    val uiState by staffViewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<BookingDto?>(null) }

    LaunchedEffect(Unit) {
        staffViewModel.loadTodayBookings()
    }

    val filteredBookings = when (selectedFilter) {
        "Pending" -> todayBookings.filter { it.status == "pending" }
        "Checked-in" -> todayBookings.filter { it.slotName == "da gui xe" }
        "Completed" -> todayBookings.filter { it.status == "completed" }
        "Unpaid" -> todayBookings.filter { it.statusPayment == "unpaid" }
        else -> todayBookings
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
                text = "Bookings Today",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { staffViewModel.loadTodayBookings() }) {
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
                label = { Text("All (${todayBookings.size})") }
            )
            FilterChip(
                selected = selectedFilter == "Pending",
                onClick = { selectedFilter = "Pending" },
                label = { Text("Pending") }
            )
            FilterChip(
                selected = selectedFilter == "Checked-in",
                onClick = { selectedFilter = "Checked-in" },
                label = { Text("Checked-in") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "Completed",
                onClick = { selectedFilter = "Completed" },
                label = { Text("Completed") }
            )
            FilterChip(
                selected = selectedFilter == "Unpaid",
                onClick = { selectedFilter = "Unpaid" },
                label = { Text("Unpaid") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bookings List
        if (uiState is StaffUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredBookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No bookings found")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBookings) { booking ->
                    BookingCard(
                        booking = booking,
                        onClick = {
                            selectedBooking = booking
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // Booking Detail Dialog
    if (showDialog && selectedBooking != null) {
        BookingDetailDialog(
            booking = selectedBooking!!,
            staffViewModel = staffViewModel,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun BookingCard(
    booking: BookingDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = booking.numberPlate ?: "N/A",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                StatusChip(status = booking.status ?: "unknown")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalParking,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${booking.parkName ?: "N/A"} - ${booking.slotName ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${booking.startTime ?: "N/A"} - ${booking.endTime ?: "N/A"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "$${booking.price ?: 0.0}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF002E5D)
                )
            }

            if (booking.statusPayment == "unpaid") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Unpaid",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "confirmed" -> Color(0xFF4CAF50) to Color.White
        "pending" -> Color(0xFFFFA726) to Color.White
        "completed" -> Color(0xFF2196F3) to Color.White
        "cancelled" -> Color(0xFFF44336) to Color.White
        else -> Color.Gray to Color.White
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BookingDetailDialog(
    booking: BookingDto,
    staffViewModel: StaffViewModel,
    onDismiss: () -> Unit
) {
    var showSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showErrorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Booking Details", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("License Plate:", booking.numberPlate ?: "N/A")
                DetailRow("Park:", booking.parkName ?: "N/A")
                DetailRow("Slot:", booking.slotName ?: "N/A")
                DetailRow("Address:", booking.address ?: "N/A")
                DetailRow("Start Time:", booking.startTime ?: "N/A")
                DetailRow("End Time:", booking.endTime ?: "N/A")
                DetailRow("Price:", "$${booking.price ?: 0.0}")
                DetailRow("Status:", booking.status ?: "N/A")
                DetailRow("Payment:", booking.statusPayment ?: "N/A")
                DetailRow("Vehicle Type:", booking.type_vehicle ?: "N/A")

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                if (booking.status != "completed") {
                    Button(
                        onClick = {
                            booking.id?.let { id ->
                                staffViewModel.checkInBooking(
                                    bookingId = id,
                                    onSuccess = {
                                        showSuccessMessage = "Check-in successful!"
                                    },
                                    onError = { error ->
                                        showErrorMessage = error
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Check-in")
                    }
                }

                if (booking.statusPayment == "unpaid") {
                    Button(
                        onClick = {
                            booking.id?.let { id ->
                                staffViewModel.updatePaymentStatus(
                                    bookingId = id,
                                    statusPayment = "paid",
                                    onSuccess = {
                                        showSuccessMessage = "Payment marked as paid!"
                                    },
                                    onError = { error ->
                                        showErrorMessage = error
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xFF2196F3))
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark as Paid")
                    }
                }

                // Success/Error Messages
                showSuccessMessage?.let { message ->
                    Text(
                        text = message,
                        color = Color.Green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                showErrorMessage?.let { message ->
                    Text(
                        text = message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}