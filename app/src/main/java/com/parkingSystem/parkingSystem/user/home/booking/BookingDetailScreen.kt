package com.parkingSystem.parkingSystem.user.home.booking

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme
import com.parkingSystem.parkingSystem.viewmodel.BookingDetailViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingDetailScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    bookingId: String
) {
    val vm: BookingDetailViewModel = viewModel(factory = viewModelFactory {
        initializer { BookingDetailViewModel(sharedPreferences) }
    })

    val booking by vm.booking.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(bookingId) {
        vm.fetchBookingDetail(bookingId)
    }

    val gradient = LocalGradientTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        navHostController.popBackStack()
                    }
            )

            Text(
                text = "Booking details",
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.extraLarge.copy(
                topStart = CornerSize(30.dp),
                topEnd = CornerSize(30.dp)
            ),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp
        ) {

            when {
                isLoading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                error != null -> {
                    CenterDetailMessage(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                booking == null -> {
                    CenterDetailMessage("No booking data found.")
                }

                else -> {
                    val b = booking!!

                    val dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

                    val createdText = runCatching {
                        OffsetDateTime.parse(b.createdAt).format(dateFmt)
                    }.getOrNull() ?: (b.createdAt ?: "--")

                    val statusColor = when (b.status?.lowercase()) {
                        "pending" -> MaterialTheme.colorScheme.primary
                        "done" -> Color(0xFF2E7D32)
                        "cancelled", "canceled" -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.onBackground
                    }
                    var showReportDialog by remember { mutableStateOf(false) }
                    var reportText by remember { mutableStateOf("") }
                    var isSendingReport by remember { mutableStateOf(false) }
                    var reportError by remember { mutableStateOf<String?>(null) }
                    var reportSuccess by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // header line
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        b.parkName ?: "Parking lot",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        b.status?.uppercase() ?: "--",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = statusColor
                                        )
                                    )
                                }

                                Text(
                                    b.address ?: "Adress: --",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                    )
                                )

                                Divider()

                                InfoRow(label = "License plate", value = b.numberPlate ?: "--")
                                InfoRow(label = "Parking position", value = b.slotName ?: "--")
                                InfoRow(label = "Vehicle type", value = b.type_vehicle ?: "--")

                                Divider()

                                InfoRow(label = "Payment method", value = b.paymentMethod ?: "--")
                                InfoRow(label = "Payment status", value = b.statusPayment ?: "--")
                                InfoRow(label = "Created at", value = createdText)

                                if (b.price != null && b.type_vehicle != null) {
                                    Divider()
                                    InfoRow(
                                        label = "Price",
                                        value = "${"%,.0f".format(b.price)}đ / ${b.type_vehicle}",
                                        bold = true
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (b.status?.lowercase() == "pending" || b.status?.lowercase() == "done") {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        navHostController.navigate("booking_qr/${b.id}")
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = "QR Code",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Show QR code",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (b.status?.lowercase() == "pending") {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        vm.cancelBooking(
                                            b.id ?: return@Button
                                        ) {
                                            navHostController.popBackStack()
                                        }
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Cancel booking",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Cancel booking", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (b.status?.lowercase() == "pending" || b.status?.lowercase() == "done") {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFA000),
                                        contentColor = Color.Black
                                    ),
                                    onClick = {
                                        showReportDialog = true
                                        reportError = null
                                        reportSuccess = false
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Complain an issue",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Complain an issue",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (showReportDialog) {
                                    AlertDialog(
                                        onDismissRequest = {
                                            if (!isSendingReport) {
                                                showReportDialog = false
                                            }
                                        },
                                        confirmButton = {
                                            if (showReportDialog) {
                                                AlertDialog(
                                                    onDismissRequest = {
                                                        if (!isSendingReport) {
                                                            showReportDialog = false
                                                        }
                                                    },
                                                    confirmButton = {
                                                        Button(
                                                            enabled = !isSendingReport && reportText.isNotBlank() && !reportSuccess,
                                                            onClick = {
                                                                val bookingIdReal = b.id ?: return@Button
                                                                val slotIdReal = b.slotId ?: ""
                                                                val userIdReal = b.userId ?: ""
                                                                if (userIdReal.isBlank()) {
                                                                    reportError = "Missing user info"
                                                                    return@Button
                                                                }

                                                                val titleReal = "Issue at slot $slotIdReal"

                                                                isSendingReport = true
                                                                reportError = null
                                                                reportSuccess = false

                                                                vm.reportIssue(
                                                                    bookingId = bookingIdReal,
                                                                    slotId = slotIdReal,
                                                                    userId = userIdReal,
                                                                    title = titleReal,
                                                                    content = reportText,
                                                                    onSuccess = {
                                                                        isSendingReport = false
                                                                        reportSuccess = true
                                                                        reportText = ""
                                                                        // ❌ KHÔNG đóng dialog ở đây nữa
                                                                        // showReportDialog = false
                                                                    },
                                                                    onError = { msg ->
                                                                        isSendingReport = false
                                                                        reportError = msg ?: "Failed to send report"
                                                                    }
                                                                )
                                                            }
                                                        ) {
                                                            if (isSendingReport) {
                                                                CircularProgressIndicator(
                                                                    strokeWidth = 2.dp,
                                                                    modifier = Modifier.size(18.dp),
                                                                    color = MaterialTheme.colorScheme.onPrimary
                                                                )
                                                            } else if (reportSuccess) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Check,
                                                                        contentDescription = "Sent",
                                                                        modifier = Modifier.size(18.dp),
                                                                        tint = Color(0xFF2E7D32)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("Sent")
                                                                }
                                                            } else {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Send,
                                                                        contentDescription = "Send report",
                                                                        modifier = Modifier.size(18.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("Send")
                                                                }
                                                            }
                                                        }
                                                    },
                                                    dismissButton = {
                                                        OutlinedButton(
                                                            enabled = !isSendingReport,
                                                            onClick = { showReportDialog = false }
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Close,
                                                                    contentDescription = "Close dialog",
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Text(
                                                                    text = if (reportSuccess) "Done" else "Close"
                                                                )
                                                            }
                                                        }
                                                    },
                                                    title = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.Center,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Warning,
                                                                contentDescription = "Warning",
                                                                tint = if (reportSuccess) Color(0xFF2E7D32) else Color.Red,
                                                                modifier = Modifier.size(24.dp)
                                                            )

                                                            Spacer(modifier = Modifier.width(8.dp))

                                                            Text(
                                                                text = if (reportSuccess) "Report sent" else "Report an issue",
                                                                style = MaterialTheme.typography.titleMedium.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 20.sp,
                                                                    color = if (reportSuccess) Color(0xFF2E7D32) else Color.Red,
                                                                    textAlign = TextAlign.Center
                                                                )
                                                            )
                                                        }
                                                    },
                                                    text = {
                                                        Column(
                                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                            if (!reportSuccess) {
                                                                Text(
                                                                    text = "Tell us what went wrong with this booking.",
                                                                    style = MaterialTheme.typography.bodyMedium
                                                                )
                                                            }

                                                            OutlinedTextField(
                                                                value = reportText,
                                                                onValueChange = { reportText = it },
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .heightIn(min = 80.dp),
                                                                placeholder = {
                                                                    Text("Enter a description of your problem")
                                                                },
                                                                shape = RoundedCornerShape(12.dp),
                                                                enabled = !isSendingReport && !reportSuccess
                                                            )

                                                            if (reportError != null) {
                                                                Text(
                                                                    text = reportError!!,
                                                                    color = MaterialTheme.colorScheme.error,
                                                                    style = MaterialTheme.typography.bodySmall
                                                                )
                                                            }

                                                            if (reportSuccess) {
                                                                Text(
                                                                    text = "Your report has been sent. Thank you.",
                                                                    color = Color(0xFF2E7D32),
                                                                    style = MaterialTheme.typography.bodySmall,
                                                                    fontWeight = FontWeight.SemiBold
                                                                )
                                                            }
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(20.dp),
                                                    containerColor = MaterialTheme.colorScheme.surface,
                                                    tonalElevation = 4.dp
                                                )
                                            }

                                        },
                                        dismissButton = {
                                            OutlinedButton(
                                                enabled = !isSendingReport,
                                                onClick = { showReportDialog = false }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Close dialog",
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Close")
                                                }
                                            }
                                        },
                                        title = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = "Warning",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(24.dp)
                                                )

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Text(
                                                    text = "Report an issue",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 25.sp,
                                                        color = Color.Red,
                                                        textAlign = TextAlign.Center
                                                    )
                                                )
                                            }
                                        },
                                        text = {
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(
                                                    text = "Tell us what went wrong with this booking.",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                OutlinedTextField(
                                                    value = reportText,
                                                    onValueChange = { reportText = it },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .heightIn(min = 80.dp),
                                                    placeholder = {
                                                        Text("Enter a description of your problem")
                                                    },
                                                    shape = RoundedCornerShape(12.dp)
                                                )

                                                if (reportError != null) {
                                                    Text(
                                                        text = reportError!!,
                                                        color = MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }

                                                if (reportSuccess) {
                                                    Text(
                                                        text = "Your report has been sent. Thank you.",
                                                        color = Color(0xFF2E7D32),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        tonalElevation = 4.dp
                                    )
                                }
                            }
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                onClick = { navHostController.popBackStack() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text("Back")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun CenterDetailMessage(
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = color,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        )
        Text(
            text = value,
            style = if (bold) {
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            textAlign = TextAlign.End
        )
    }
}
