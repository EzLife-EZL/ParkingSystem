package com.parkingSystem.parkingSystem.staff

import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.parkingSystem.parkingSystem.user.home.startscreen.SignIn

// ============ STAFF HISTORY SCREEN ============
@Composable
fun StaffHistoryScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Lịch sử hoạt động",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Hôm nay",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(5) { index ->
            HistoryCard(
                title = "Check-in xe #${index + 1}",
                time = "10:${30 + index * 5} AM",
                status = if (index % 2 == 0) "Thành công" else "Đang xử lý"
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hôm qua",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(3) { index ->
            HistoryCard(
                title = "Check-out xe #${index + 1}",
                time = "0${3 + index}:15 PM",
                status = "Hoàn thành"
            )
        }
    }
}

@Composable
fun HistoryCard(
    title: String,
    time: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = when (status) {
                    "Thành công", "Hoàn thành" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.tertiary
                },
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = status,
                fontSize = 14.sp,
                color = when (status) {
                    "Thành công", "Hoàn thành" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.tertiary
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StaffSettingScreen(
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Cài đặt",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Profile Section
        item {
            Text(
                text = "Tài khoản",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            SettingCard(
                icon = Icons.Default.Person,
                title = "Thông tin cá nhân",
                subtitle = "Xem và chỉnh sửa thông tin",
                onClick = { /* TODO: Navigate to profile */ }
            )
        }

        item {
            SettingCard(
                icon = Icons.Default.Lock,
                title = "Đổi mật khẩu",
                subtitle = "Cập nhật mật khẩu của bạn",
                onClick = { /* TODO: Change password */ }
            )
        }

        // App Settings Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ứng dụng",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            SettingCard(
                icon = Icons.Default.Notifications,
                title = "Thông báo",
                subtitle = "Quản lý thông báo",
                onClick = { /* TODO: Notification settings */ }
            )
        }

        item {
            SettingCard(
                icon = Icons.Default.Language,
                title = "Ngôn ngữ",
                subtitle = "Tiếng Việt",
                onClick = { /* TODO: Language settings */ }
            )
        }

        // About Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Khác",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            SettingCard(
                icon = Icons.Default.Info,
                title = "Về ứng dụng",
                subtitle = "Phiên bản 1.0.0",
                onClick = { /* TODO: About */ }
            )
        }

        item {
            SettingCard(
                icon = Icons.Default.Help,
                title = "Trợ giúp",
                subtitle = "Câu hỏi thường gặp và hỗ trợ",
                onClick = { /* TODO: Help */ }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Logout Button
        item {
            Button(
                onClick = {
                    sharedPreferences.edit().clear().apply()
                    val intent = Intent(context, SignIn::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============ PARKING MANAGEMENT SCREEN ============
@Composable
fun ParkingManagementScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Quản lý bãi đỗ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(10) { index ->
                ParkingSlotCard(
                    slotNumber = "A${index + 1}",
                    status = if (index % 3 == 0) "Trống" else if (index % 3 == 1) "Đã đặt" else "Đang sử dụng",
                    vehicleInfo = if (index % 3 != 0) "29A-12345" else null
                )
            }
        }
    }
}

@Composable
fun ParkingSlotCard(
    slotNumber: String,
    status: String,
    vehicleInfo: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                "Trống" -> MaterialTheme.colorScheme.primaryContainer
                "Đã đặt" -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slotNumber,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (vehicleInfo != null) {
                    Text(
                        text = "Biển số: $vehicleInfo",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = when (status) {
                    "Trống" -> Icons.Default.CheckCircle
                    "Đã đặt" -> Icons.Default.Schedule
                    else -> Icons.Default.DirectionsCar
                },
                contentDescription = null,
                tint = when (status) {
                    "Trống" -> MaterialTheme.colorScheme.primary
                    "Đã đặt" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

// ============ BOOKING MANAGEMENT SCREEN ============
@Composable
fun BookingManagementScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Quản lý đặt chỗ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(8) { index ->
                BookingCard(
                    bookingId = "#BK${1000 + index}",
                    customerName = "Khách hàng ${index + 1}",
                    slotNumber = "A${index + 1}",
                    time = "10:${30 + index * 5} - 12:${30 + index * 5}",
                    status = if (index % 2 == 0) "Đang chờ" else "Đã xác nhận"
                )
            }
        }
    }
}

@Composable
fun BookingCard(
    bookingId: String,
    customerName: String,
    slotNumber: String,
    time: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
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
                    text = bookingId,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (status == "Đã xác nhận")
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = customerName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocalParking,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Vị trí: $slotNumber",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}