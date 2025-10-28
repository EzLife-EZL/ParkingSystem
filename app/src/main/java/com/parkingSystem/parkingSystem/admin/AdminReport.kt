package com.parkingSystem.parkingSystem.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.parkingSystem.parkingSystem.responsemodel.ReportResponse
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

data class ComplaintData(
    val reportId: String,
    val bookingId: String,
    val userId: String,
    val slotId: String,
    val title: String,
    val content: String,
    val status: String,
    val createdAt: String
)

@Composable
fun ReportManagerScreen() {
    val reportList = remember { mutableStateListOf<ComplaintData>() }
    val coroutineScope = rememberCoroutineScope()
    var selectedComplaint by remember { mutableStateOf<ComplaintData?>(null) }
    val navController = rememberNavController()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response: List<ReportResponse> =
                    RetrofitInstance.reportService.getAllReports()

                android.util.Log.d(
                    "ReportManagerScreen",
                    "Fetched ${response.size} reports from API"
                )

                reportList.clear()

                response.forEach { report ->
                    android.util.Log.d("ReportManagerScreen", "report item: $report")

                    reportList.add(
                        ComplaintData(
                            reportId = report._id ?: "",
                            bookingId = report.bookingId ?: "",
                            userId = report.userId ?: "",
                            slotId = report.slotId ?: "",
                            title = report.title ?: "(no title)",
                            content = report.content ?: "(no content)",
                            status = report.status ?: "opened",
                            createdAt = report.createdAt ?: "--"
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ReportManagerScreen", "Failed to load reports", e)
                Toast.makeText(
                    context,
                    "Failed to load reports: ${e.message ?: "unknown"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    NavHost(navController = navController, startDestination = "ReportMain") {

        composable("ReportMain") {
            ReportManagerContent(
                reportList = reportList,
                navController = navController,
                selectedComplaint = selectedComplaint,
                onSelectComplaint = { selectedComplaint = it },
                onCloseDetail = { selectedComplaint = null },
                onDeleteLocal = { toDelete ->
                    reportList.remove(toDelete)
                }
            )
        }

        composable("RespondScreen/{reportId}") { backStackEntry ->
            val rid = backStackEntry.arguments?.getString("reportId") ?: ""
            val complaint = reportList.find { it.reportId == rid }
            if (complaint != null) {
                ReportResponseScreen(
                    complaint = complaint,
                    onBack = { navController.popBackStack() },
                    onResponseSuccess = {
                        val idx = reportList.indexOfFirst { it.reportId == complaint.reportId }
                        if (idx != -1) {
                            val old = reportList[idx]
                            val updated = old.copy(status = "closed")
                            reportList[idx] = updated
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ReportManagerContent(
    reportList: MutableList<ComplaintData>,
    navController: NavController,
    selectedComplaint: ComplaintData?,
    onSelectComplaint: (ComplaintData?) -> Unit,
    onCloseDetail: () -> Unit,
    onDeleteLocal: (ComplaintData) -> Unit
) {
    val backgroundColor = Color(0xFFF4F5F7)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "List of reports",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                ComplaintStatsScreen(reportList = reportList)

                Text(
                    text = "Report Management",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                )

                TableReport(
                    reportList = reportList,
                    onDetailClick = { c -> onSelectComplaint(c) },
                    onRespondClick = { c ->
                        navController.navigate("RespondScreen/${c.reportId}")
                    },
                    onDeleteClick = { c ->
                        coroutineScope.launch {
                            if (c.status == "opened") {
                                Toast.makeText(
                                    context,
                                    "Unable to delete an opened report",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }

                            val result =
                                RetrofitInstance.reportService.deleteReport(c.reportId)

                            if (result.isSuccessful) {
                                onDeleteLocal(c)
                                Toast.makeText(
                                    context,
                                    "Report deleted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Delete failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }
        }
    }

    // popup detail
    if (selectedComplaint != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
                    .padding(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Report Detail",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Close",
                            color = Color.Red,
                            modifier = Modifier.clickable { onCloseDetail() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailRow(label = "User ID",    value = selectedComplaint.userId)
                    DetailRow(label = "Booking ID", value = selectedComplaint.bookingId)
                    DetailRow(label = "Slot ID",    value = selectedComplaint.slotId)
                    DetailRow(label = "Title",      value = selectedComplaint.title)
                    DetailRow(label = "Content",    value = selectedComplaint.content)
                    DetailRow(label = "Status",     value = selectedComplaint.status)
                    DetailRow(label = "Created",    value = selectedComplaint.createdAt)
                }
            }
        }
    }
}

@Composable
fun TableReport(
    reportList: MutableList<ComplaintData>,
    onDetailClick: (ComplaintData) -> Unit,
    onRespondClick: (ComplaintData) -> Unit,
    onDeleteClick: (ComplaintData) -> Unit
) {
    LazyRow {
        item {
            Column {
                // header row
                Row(
                    Modifier
                        .background(Color(0xFF002E5D))
                        .padding(vertical = 8.dp)
                ) {
                    ComplaintTableHeader()
                }

                // body rows
                reportList.forEachIndexed { index, complaint ->
                    val bgColor =
                        if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                    var expanded by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TableCell((index + 1).toString(), width = 60.dp)
                        TableCell(complaint.userId, width = 120.dp)
                        TableCell(complaint.title, width = 180.dp)
                        TableCell(complaint.status, width = 100.dp)
                        TableCell(
                            complaint.createdAt.take(16),
                            width = 140.dp
                        )

                        Box(
                            modifier = Modifier.width(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "Menu"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        Color.LightGray,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(Color.White)
                            ) {
                                // Detail
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Detail")
                                        }
                                    },
                                    onClick = {
                                        expanded = false
                                        onDetailClick(complaint)
                                    }
                                )

                                // Respond
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Response")
                                        }
                                    },
                                    onClick = {
                                        expanded = false
                                        onRespondClick(complaint)
                                    }
                                )

                                // Delete
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Delete")
                                        }
                                    },
                                    onClick = {
                                        expanded = false
                                        onDeleteClick(complaint)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComplaintTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF002E5D))
    ) {
        TableCell(text = "No.",     isHeader = true, width = 60.dp)
        TableCell(text = "User ID", isHeader = true, width = 120.dp)
        TableCell(text = "Title",   isHeader = true, width = 180.dp)
        TableCell(text = "Status",  isHeader = true, width = 100.dp)
        TableCell(text = "Created", isHeader = true, width = 140.dp)
        TableCell(text = "Action",  isHeader = true, width = 100.dp)
    }
}

@Composable
fun TableCell(
    text: String,
    width: Dp,
    isHeader: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = if (isHeader) Color.White else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
fun ComplaintStatsScreen(reportList: List<ComplaintData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ComplaintCard(
            icon = Icons.Default.LocalOffer,
            iconColor = Color(0xFF6A5ACD),
            number = reportList.size.toString(),
            label = "Total reports"
        )

        ComplaintCard(
            icon = Icons.Default.AccessTime,
            iconColor = Color(0xFFFFC107),
            number = reportList.count { it.status == "opened" }.toString(),
            label = "Open reports"
        )

        ComplaintCard(
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF009688),
            number = reportList.count { it.status == "closed" }.toString(),
            label = "Closed reports"
        )
    }
}

@Composable
fun ComplaintCard(
    icon: ImageVector,
    iconColor: Color,
    number: String,
    label: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = number,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$label: ")
            }
            append(value)
        },
        fontSize = 16.sp
    )
}
