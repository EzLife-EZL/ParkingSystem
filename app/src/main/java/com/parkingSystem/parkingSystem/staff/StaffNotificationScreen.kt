package com.parkingSystem.parkingSystem.staff

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.parkingSystem.parkingSystem.responsemodel.NotificationResponse
import com.parkingSystem.parkingSystem.viewmodel.NotificationViewModel
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffNotificationScreen(context: Context, navHostController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val notificationViewModel: NotificationViewModel = viewModel(factory = viewModelFactory {
        initializer { NotificationViewModel(sharedPreferences) }
    })

    val notifications by notificationViewModel.notifications.collectAsState()
    var userId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        if (userId.isNotEmpty()) {
            notificationViewModel.fetchNotificationByStaffId(userId)
        }
    }

    Scaffold(
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(60.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFFBDBDBD)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "No notifications yet!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You'll be notified about important updates",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }
        } else {
            // Danh sách thông báo
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Recent Updates",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(notifications) { notification ->
                    StaffNotificationItem(
                        notification,
                        onClick = {
                            println("Click notification: ${notification.id}")
                            notificationViewModel.updateReadStatus(notification.id)
                            navHostController.navigate(notification.navPath)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StaffNotificationItem(
    notification: NotificationResponse,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        colors =
            if (notification.isRead) {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
            else{
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                text = "Notification",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.content ?: "",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = notification.createdAt,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

