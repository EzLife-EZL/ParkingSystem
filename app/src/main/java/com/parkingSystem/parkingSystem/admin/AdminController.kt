package com.parkingSystem.parkingSystem.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme
import com.parkingSystem.parkingSystem.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = viewModel()
) {
    val gradient: Brush = LocalGradientTheme.current.primary
    LaunchedEffect(Unit) {
        viewModel.refreshAllData()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        item {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Parking Management System",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ParkingLotStatusSection(viewModel = viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            RevenueReportSection(viewModel = viewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ParkingLotStatusSection(
    viewModel: AdminViewModel
) {
    val overview by viewModel.parkingOverview.collectAsStateWithLifecycle()
    val isLoading by viewModel.isParkingOverviewLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.parkingOverviewError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Overview",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        value = overview?.parkedVehicles?.toString() ?: "0",
                        title = "Parked Vehicles",
                        backgroundColor = Brush.linearGradient(
                            colors = listOf(Color(0xFF36D1DC), Color(0xFF5B86E5))
                        ),
                        icon = R.drawable.submit_arrow,
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        value = overview?.availableSpots?.toString() ?: "0",
                        title = "Available Spots",
                        backgroundColor = Brush.linearGradient(
                            colors = listOf(Color(0xFF11998E), Color(0xFF38EF7D))
                        ),
                        icon = R.drawable.submit_arrow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    value: String,
    title: String,
    backgroundColor: Brush,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = value,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Details",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueReportSection(
    viewModel: AdminViewModel
) {
    val revenueData by viewModel.revenueReport.collectAsStateWithLifecycle()
    val isLoading by viewModel.isRevenueLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.revenueError.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        return formatter.format(amount)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Revenue Report",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF2D3748)
                )
                FilterDropdown(
                    options = listOf("Day", "Month", "Year"),
                    selectedOption = selectedFilter.replaceFirstChar { it },
                    onOptionSelected = { viewModel.updateFilter(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Failed to load revenue data",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
                revenueData != null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                revenueData!!.currentPeriod.label,
                                color = Color(0xFF718096),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatCurrency(revenueData!!.currentPeriod.amount),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = Color(0xFF2D3748)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (revenueData!!.isPositiveGrowth)
                                                Color(0xFFD1FAE5)
                                            else
                                                Color(0xFFFEE2E2),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "${if (revenueData!!.isPositiveGrowth) "+" else "-"}${
                                            String.format(
                                                "%.1f",
                                                revenueData!!.growthPercentage
                                            )
                                        }%",
                                        color = if (revenueData!!.isPositiveGrowth)
                                            Color(0xFF059669)
                                        else
                                            Color(0xFFDC2626),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                revenueData!!.previousPeriod.label,
                                color = Color(0xFF718096),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatCurrency(revenueData!!.previousPeriod.amount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = Color(0xFF2D3748)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                if (revenueData!!.isPositiveGrowth)
                                    Color(0xFFF0FDF4)
                                else
                                    Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "${if (revenueData!!.isPositiveGrowth) "↑" else "↓"} ${
                                String.format(
                                    "%.1f",
                                    revenueData!!.growthPercentage
                                )
                            }%",
                            color = if (revenueData!!.isPositiveGrowth)
                                Color(0xFF059669)
                            else
                                Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            revenueData!!.comparisonText,
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF3F4F6))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                selectedOption,
                color = Color(0xFF4F46E5),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color(0xFF4F46E5),
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = if (option == selectedOption) Color(0xFF4F46E5) else Color(0xFF374151),
                            fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}