package com.parkingSystem.parkingSystem.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.text.style.TextAlign
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHistoryScreen(staffViewModel: StaffViewModel) {
    val allBookings by staffViewModel.allBookings.collectAsState()
    val uiState by staffViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Confirmed", "Done", "Cancelled")

    LaunchedEffect(Unit) {
        staffViewModel.loadAllBookings()
    }

    val filteredBookings = remember(selectedTab, allBookings) {
        when (selectedTab) {
            0 -> allBookings // All
            1 -> allBookings.filter { it.status?.lowercase() == "confirmed" }
            2 -> allBookings.filter { it.status?.lowercase() == "completed" || it.status?.lowercase() == "done" }
            3 -> allBookings.filter { it.status?.lowercase() == "cancelled" }
            else -> allBookings
        }
    }

    val gradient = LocalGradientTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient.primary)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Booking Management",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            IconButton(
                onClick = { staffViewModel.loadAllBookings() },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Surface with rounded top corners
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
            when (uiState) {
                is StaffUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Loading bookings...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Tab Row
                        TabRow(
                            selectedTabIndex = selectedTab,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTab])
                                        .height(3.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                val isSelected = selectedTab == index
                                val count = when (index) {
                                    0 -> allBookings.size
                                    1 -> allBookings.count { it.status?.lowercase() == "confirmed" }
                                    2 -> allBookings.count {
                                        it.status?.lowercase() == "completed" ||
                                                it.status?.lowercase() == "done"
                                    }
                                    3 -> allBookings.count { it.status?.lowercase() == "cancelled" }
                                    else -> 0
                                }

                                Tab(
                                    selected = isSelected,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected)
                                                        FontWeight.Bold
                                                    else
                                                        FontWeight.Normal,
                                                    color = if (isSelected)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                                )
                                            )
                                            if (count > 0) {
                                                Text(
                                                    text = "($count)",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isSelected)
                                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                                        else
                                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        // Content
                        if (filteredBookings.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                    )
                                    Text(
                                        text = "No bookings found",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = when (selectedTab) {
                                            1 -> "No confirmed bookings yet"
                                            2 -> "No completed bookings yet"
                                            3 -> "No cancelled bookings yet"
                                            else -> "Pull to refresh"
                                        },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 24.dp,
                                    top = 12.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = filteredBookings,
                                    key = { it.id ?: it.hashCode() }
                                ) { booking ->
                                    BookingCard(
                                        booking = booking,
                                        onClick = { /* Handle click */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
