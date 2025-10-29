package com.parkingSystem.parkingSystem.admin

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme
import com.parkingSystem.parkingSystem.viewmodel.ParkingViewModel
import kotlin.math.abs

@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboardScreen() {
    AdminDashboardScreen()
}

@Composable
fun AdminDashboardScreen() {
    val gradient: Brush = LocalGradientTheme.current.primary
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("parking_prefs", Context.MODE_PRIVATE)
    }
    val parkingViewModel = remember { ParkingViewModel(sharedPreferences) }

    LaunchedEffect(Unit) {
        parkingViewModel.getParkingOverView()
        parkingViewModel.getRevenueReport()
        parkingViewModel.getRevenueByVehicleType()
    }

    val overview by parkingViewModel.parkingOverview.collectAsState()
    val isLoading by parkingViewModel.isLoading.collectAsState()
    val error by parkingViewModel.error.collectAsState()
    val revenueReport by parkingViewModel.revenueReport.collectAsState()
    val revenueData by parkingViewModel.revenueByVehicleType.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        item {
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Error: $error",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    ParkingLotStatusSection(overview = overview)
                    Spacer(modifier = Modifier.height(16.dp))
                    RevenueReportScreen(
                        revenueReport = revenueReport,
                        onPeriodChange = { period ->
                            parkingViewModel.getRevenueReport(period.lowercase())
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    VehicleTypeRevenueCard(revenueData = revenueData)
                }
            }
        }
    }
}

@Composable
fun ParkingLotStatusSection(overview: com.parkingSystem.parkingSystem.responsemodel.ParkingOverview?) {
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
        overview?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    value = it.totalSpots.toString(),
                    title = "Total Spots",
                    backgroundColor = Brush.linearGradient(
                        colors = listOf(Color(0xFFF093FB), Color(0xFFF5576C))
                    ),
                    icon = R.drawable.submit_arrow,
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    value = "${String.format("%.1f", it.occupancyRate)}%",
                    title = "Occupancy Rate",
                    backgroundColor = Brush.linearGradient(
                        colors = listOf(Color(0xFFFA709A), Color(0xFFFEE140))
                    ),
                    icon = R.drawable.submit_arrow,
                    modifier = Modifier.weight(1f)
                )
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
fun RevenueReportScreen(
    revenueReport: com.parkingSystem.parkingSystem.responsemodel.RevenueReport?,
    onPeriodChange: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("") }

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
                    options = listOf("Day", "Week", "Month", "Year"),
                    selectedOption = selectedFilter,
                    onOptionSelected = {
                        selectedFilter = it
                        onPeriodChange(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Current Period
                Column {
                    Text(
                        revenueReport?.currentPeriod?.label ?: "This Period",
                        color = Color(0xFF718096),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${String.format("%,.0f", revenueReport?.currentPeriod?.amount ?: 0.0)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color(0xFF2D3748)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Growth badge mini
                        revenueReport?.let { report ->
                            val bgColor = if (report.isPositiveGrowth) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                            val textColor = if (report.isPositiveGrowth) Color(0xFF059669) else Color(0xFFDC2626)
                            val arrow = if (report.isPositiveGrowth) "+" else ""

                            Box(
                                modifier = Modifier
                                    .background(bgColor, shape = RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "$arrow${String.format("%.1f", report.growthPercentage)}%",
                                    color = textColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Previous Period
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        revenueReport?.previousPeriod?.label ?: "Last Period",
                        color = Color(0xFF718096),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%,.0f", revenueReport?.previousPeriod?.amount ?: 0.0)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF2D3748)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Comparison Text + Trend Chart
            revenueReport?.let { report ->
                val bgColor = if (report.isPositiveGrowth) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                val textColor = if (report.isPositiveGrowth) Color(0xFF059669) else Color(0xFFDC2626)
                val arrow = if (report.isPositiveGrowth) "↑" else "↓"

                // summary row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(bgColor, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "$arrow ${String.format("%.1f", report.growthPercentage)}%",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        report.comparisonText,
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Build data cho mini chart: previous vs current
                val chartData = listOf(
                    report.previousPeriod.label to report.previousPeriod.amount.toFloat(),
                    report.currentPeriod.label to report.currentPeriod.amount.toFloat()
                )

                val valuesOnly = chartData.map { it.second }
                val yMin = 0f
                val rawMax = valuesOnly.maxOrNull() ?: 1f
                val yMax = if (rawMax == 0f) 1f else rawMax

                val yTicks = 4
                val yTickValues = (0..yTicks).map { step ->
                    val ratio = step / yTicks.toFloat()
                    yMin + (yMax - yMin) * (1f - ratio)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {

                        // Y-axis labels
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(40.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End
                        ) {
                            yTickValues.forEach { tickValue ->
                                Text(
                                    text = String.format("%,.0f", tickValue),
                                    fontSize = 10.sp,
                                    color = Color(0xFF9CA3AF),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Chart box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFFAFAFA), shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val graphWidth = size.width
                                val graphHeight = size.height

                                val points = chartData.mapIndexed { index, (_, value) ->
                                    val x = if (chartData.size > 1) {
                                        (graphWidth / (chartData.size - 1)) * index
                                    } else {
                                        graphWidth / 2f
                                    }

                                    val normalized = if (yMax - yMin > 0f) {
                                        (value - yMin) / (yMax - yMin)
                                    } else {
                                        0.5f
                                    }

                                    val y = graphHeight -
                                            (graphHeight * 0.15f) -
                                            (normalized * graphHeight * 0.7f)

                                    x to y
                                }


                                for (i in 0..yTicks) {
                                    val ratio = i / yTicks.toFloat()
                                    val yLine = graphHeight * 0.15f + (graphHeight * 0.7f) * ratio
                                    val yCanvas = graphHeight - yLine

                                    drawLine(
                                        color = Color(0xFFE5E7EB),
                                        start = androidx.compose.ui.geometry.Offset(0f, yCanvas),
                                        end = androidx.compose.ui.geometry.Offset(graphWidth, yCanvas),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                                if (points.size > 1) {
                                    val p0 = points[0]
                                    val p1 = points[1]

                                    val growthColor = if (report.isPositiveGrowth) {
                                        Color(0xFF10B981)
                                    } else {
                                        Color(0xFFEF4444)
                                    }
                                    val fillPath = Path().apply {
                                        moveTo(p0.first, graphHeight)
                                        lineTo(p0.first, p0.second)
                                        lineTo(p1.first, p1.second)
                                        lineTo(p1.first, graphHeight)
                                        close()
                                    }
                                    drawPath(
                                        path = fillPath,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                growthColor.copy(alpha = 0.28f),
                                                growthColor.copy(alpha = 0.05f)
                                            )
                                        )
                                    )

                                    // line nối 2 period
                                    drawLine(
                                        color = growthColor,
                                        start = androidx.compose.ui.geometry.Offset(p0.first, p0.second),
                                        end = androidx.compose.ui.geometry.Offset(p1.first, p1.second),
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }

                                // vẽ chấm data
                                val pointColor = if (report.isPositiveGrowth) {
                                    Color(0xFF10B981)
                                } else {
                                    Color(0xFFEF4444)
                                }

                                points.forEach { (x, y) ->
                                    // vòng trắng ngoài
                                    drawCircle(
                                        color = Color.White,
                                        radius = 8.dp.toPx(),
                                        center = androidx.compose.ui.geometry.Offset(x, y)
                                    )
                                    // chấm màu
                                    drawCircle(
                                        color = pointColor,
                                        radius = 5.dp.toPx(),
                                        center = androidx.compose.ui.geometry.Offset(x, y)
                                    )
                                }
                            }

                            // tooltip tổng quát Peak (không gắn đúng toạ độ point để tránh phức tạp)
                            val maxValueIndex = valuesOnly.indexOfFirst { it == yMax }
                            if (maxValueIndex >= 0 && yMax > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.TopStart
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .background(
                                                Color(0xFF1F2937),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Peak",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = String.format("%,.0f", yMax),
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Nhãn X (previous vs current)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    chartData.forEachIndexed { index, (label, _) ->
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium,
                            textAlign = when (index) {
                                0 -> TextAlign.Start
                                chartData.size - 1 -> TextAlign.End
                                else -> TextAlign.Center
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } ?: run {
                // fallback nếu không có report
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF0FDF4), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "↑ 0%",
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Compared to previous period",
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
@Composable
fun VehicleTypeRevenueCard(
    revenueData: com.parkingSystem.parkingSystem.responsemodel.RevenueResponse?
) {
    fun List<com.parkingSystem.parkingSystem.responsemodel.RevenueVehicleType>.visibleSeries():
            List<com.parkingSystem.parkingSystem.responsemodel.RevenueVehicleType> {
        return this.filter { !it.typeVehicle.equals("Unknown", ignoreCase = true) }
    }

    var selectedFilter by remember { mutableStateOf("") }

    LaunchedEffect(revenueData) {
        if (revenueData != null && revenueData.series.isNotEmpty() && selectedFilter.isEmpty()) {
            val cleanList = revenueData.series.visibleSeries()
            selectedFilter = if (cleanList.isNotEmpty()) {
                cleanList.first().typeVehicle
            } else {
                ""
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header với icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Revenue by\nVehicle Type",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedFilter.isNotEmpty()) selectedFilter else "No vehicle type",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.3.sp
                    )
                }

                revenueData?.let { data ->
                    val visibleTypes = data.series
                        .map { it.typeVehicle }
                        .filter { !it.equals("Unknown", ignoreCase = true) }

                    if (visibleTypes.isNotEmpty()) {
                        FilterDropdown(
                            options = visibleTypes,
                            selectedOption = if (selectedFilter.isNotEmpty())
                                selectedFilter
                            else
                                visibleTypes.first(),
                            onOptionSelected = { selectedFilter = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val selectedVehicleData = revenueData?.let { data ->
                val validSeries = data.series.visibleSeries()
                if (validSeries.isEmpty()) {
                    null
                } else {
                    validSeries.find {
                        it.typeVehicle.equals(selectedFilter, ignoreCase = true)
                    } ?: validSeries.first()
                }
            }

            selectedVehicleData?.let { vehicleData ->
                val chartData: List<Pair<String, Float>> =
                    revenueData!!.labels.mapIndexed { index, label ->
                        val valueFloat = (vehicleData.months.getOrNull(index) ?: 0.0).toFloat()
                        label to valueFloat
                    }

                val valuesOnly = chartData.map { it.second }
                val totalRevenue = valuesOnly.sum()
                val avgRevenue = if (valuesOnly.isNotEmpty()) {
                    totalRevenue / valuesOnly.size
                } else 0f

                // Stats Grid với modern design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModernStatBox(
                        label = "Total Revenue",
                        value = String.format("%,.0f", totalRevenue),
                        color = Color(0xFF3B82F6),
                        icon = "💰",
                        modifier = Modifier.weight(1f)
                    )
                    ModernStatBox(
                        label = "Average / mo",
                        value = String.format("%,.0f", avgRevenue),
                        color = Color(0xFF10B981),
                        icon = "📊",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModernStatBox(
                        label = "Total Bookings",
                        value = vehicleData.totalBookings.toString(),
                        color = Color(0xFF8B5CF6),
                        icon = "🎫",
                        modifier = Modifier.weight(1f)
                    )
                    ModernStatBox(
                        label = "Paid Bookings",
                        value = vehicleData.paidBookings.toString(),
                        color = Color(0xFFEC4899),
                        icon = "✓",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Growth indicator
                val growthColor = if (vehicleData.growth >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                val growthBgColor = if (vehicleData.growth >= 0) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                val growthIcon = if (vehicleData.growth >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(growthBgColor, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = growthIcon,
                        contentDescription = null,
                        tint = growthColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${String.format("%.1f", abs(vehicleData.growth))}% ${if (vehicleData.growth >= 0) "growth" else "decline"}",
                            color = growthColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "vs last month",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Modern Chart
                VehicleRevenueChart(chartData = chartData)

            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📊",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No data available",
                            color = Color(0xFF64748B),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStatBox(
    label: String,
    value: String,
    color: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = icon,
                    fontSize = 15.sp
                )
                Text(
                    value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = color,
                    letterSpacing = (-0.5).sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun VehicleRevenueChart(chartData: List<Pair<String, Float>>) {
    val valuesOnly = chartData.map { it.second }
    val maxValue = valuesOnly.maxOrNull() ?: 0f
    val minValue = valuesOnly.minOrNull() ?: 0f
    val range = (maxValue - minValue).let { if (it == 0f) 1f else it }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFBFC)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val points = chartData.mapIndexed { index, (_, value) ->
                        val x = if (chartData.size > 1) {
                            (w / (chartData.size - 1)) * index
                        } else {
                            w / 2f
                        }
                        val norm = if (range > 0f) (value - minValue) / range else 0.5f
                        val y = h - (h * 0.15f) - (norm * h * 0.7f)
                        x to y
                    }

                    // Grid lines
                    for (i in 0..4) {
                        val yLine = h * 0.15f + (h * 0.7f) * (i / 4f)
                        val yCanvas = h - yLine
                        drawLine(
                            color = Color(0xFFE5E7EB),
                            start = androidx.compose.ui.geometry.Offset(0f, yCanvas),
                            end = androidx.compose.ui.geometry.Offset(w, yCanvas),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Smooth curve path
                    if (points.size > 1) {
                        val lineColor = Color(0xFF3B82F6)

                        // Area fill with gradient
                        val areaPath = Path().apply {
                            moveTo(points.first().first, h)
                            lineTo(points.first().first, points.first().second)

                            for (i in 0 until points.size - 1) {
                                val current = points[i]
                                val next = points[i + 1]
                                val cp1X = current.first + (next.first - current.first) / 3f
                                val cp2X = current.first + 2f * (next.first - current.first) / 3f
                                cubicTo(
                                    cp1X, current.second,
                                    cp2X, next.second,
                                    next.first, next.second
                                )
                            }

                            lineTo(points.last().first, h)
                            close()
                        }

                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    lineColor.copy(alpha = 0.25f),
                                    lineColor.copy(alpha = 0.05f)
                                )
                            )
                        )

                        // Smooth line
                        val linePath = Path().apply {
                            moveTo(points.first().first, points.first().second)
                            for (i in 0 until points.size - 1) {
                                val current = points[i]
                                val next = points[i + 1]
                                val cp1X = current.first + (next.first - current.first) / 3f
                                val cp2X = current.first + 2f * (next.first - current.first) / 3f
                                cubicTo(
                                    cp1X, current.second,
                                    cp2X, next.second,
                                    next.first, next.second
                                )
                            }
                        }

                        drawPath(
                            path = linePath,
                            color = lineColor,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }

                    // Data points với glow effect
                    points.forEach { (x, y) ->
                        // Outer glow
                        drawCircle(
                            color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                            radius = 12.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                        // White ring
                        drawCircle(
                            color = Color.White,
                            radius = 8.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                        // Inner dot
                        drawCircle(
                            color = Color(0xFF3B82F6),
                            radius = 5.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // X-axis labels (show only some to avoid clutter)
            val visibleLabelIndexes = when {
                chartData.size <= 3 -> chartData.indices.toList()
                else -> listOf(0, chartData.size / 2, chartData.lastIndex)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                chartData.forEachIndexed { index, (label, _) ->
                    Text(
                        text = if (index in visibleLabelIndexes) label else "",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium,
                        textAlign = when (index) {
                            0 -> TextAlign.Start
                            chartData.size - 1 -> TextAlign.End
                            else -> TextAlign.Center
                        },
                        modifier = Modifier.weight(1f)
                    )
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
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Text(
                    selectedOption.ifEmpty { "Select" },
                    color = MaterialTheme.colorScheme.background,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint =  MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(12.dp))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = if (option == selectedOption)
                                MaterialTheme.colorScheme.primary
                            else Color(0xFF374151),
                            fontWeight = if (option == selectedOption)
                                FontWeight.Bold
                            else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (option == selectedOption)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else Color.Transparent
                    )
                )
            }
        }
    }
}
