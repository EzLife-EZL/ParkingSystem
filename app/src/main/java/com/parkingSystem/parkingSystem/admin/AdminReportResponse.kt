package com.parkingSystem.parkingSystem.admin

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import com.parkingSystem.parkingSystem.requestmodel.AdminResponseRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportResponseScreen(
    complaint: ComplaintData,
    onBack: () -> Unit,
    onResponseSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var responseContent by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val dateString = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F5F7)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = Color(0xFF002E5D),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(enabled = !isSending) { onBack() }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Reply to Complaint",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF002E5D),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Complaint Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF002E5D)
                        )

                        Divider(color = Color(0xFFE5E7EB))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoColumn(
                                label = "User ID",
                                value = complaint.userId,
                                modifier = Modifier.weight(1f)
                            )
                            InfoColumn(
                                label = "Booking ID",
                                value = complaint.bookingId,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoColumn(
                                label = "Slot ID",
                                value = complaint.slotId,
                                modifier = Modifier.weight(1f)
                            )
                            InfoColumn(
                                label = "Status",
                                value = complaint.status,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        InfoColumn(
                            label = "Created At",
                            value = complaint.createdAt
                        )

                        Divider(color = Color(0xFFE5E7EB))

                        InfoColumn(
                            label = "Title",
                            value = complaint.title
                        )

                        InfoColumn(
                            label = "User Message",
                            value = complaint.content,
                            isLongText = true
                        )
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Your Response",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF002E5D)
                        )

                        Divider(color = Color(0xFFE5E7EB))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoColumn(
                                label = "From",
                                value = "Admin",
                                modifier = Modifier.weight(1f)
                            )
                            InfoColumn(
                                label = "To",
                                value = complaint.userId,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        InfoColumn(
                            label = "Response Date",
                            value = dateString
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Feedback Content *",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            OutlinedTextField(
                                value = responseContent,
                                onValueChange = { responseContent = it },
                                placeholder = {
                                    Text(
                                        "Enter your response to the user's complain...",
                                        color = Color(0xFF9CA3AF)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 140.dp),
                                enabled = !isSending,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF002E5D),
                                    unfocusedBorderColor = Color(0xFFD1D5DB),
                                    disabledBorderColor = Color(0xFFE5E7EB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                maxLines = 8
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        if (isSending) return@Button
                        scope.launch {
                            if (responseContent.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please enter your feedback",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }

                            isSending = true
                            try {
                                val result = RetrofitInstance.reportService.sendAdminResponse(
                                    id = complaint.reportId,
                                    response = AdminResponseRequest(
                                        responseContent = responseContent,
                                        responseTime = dateString
                                    )
                                )

                                if (result.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Feedback sent successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    onResponseSuccess()
                                    onBack()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to send feedback (${result.code()})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message ?: "Unknown error"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isSending = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isSending && responseContent.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF002E5D),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFD1D5DB),
                        disabledContentColor = Color(0xFF9CA3AF)
                    )
                ) {
                    if (isSending) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Sending...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Text(
                            text = "Send Feedback",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLongText: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF111827),
            maxLines = if (isLongText) Int.MAX_VALUE else 2,
            overflow = if (isLongText) TextOverflow.Visible else TextOverflow.Ellipsis
        )
    }
}