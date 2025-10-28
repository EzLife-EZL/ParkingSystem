@file:OptIn(ExperimentalMaterial3Api::class)

package com.parkingSystem.parkingSystem.user.notification
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.parkingSystem.parkingSystem.responsemodel.NotificationResponse
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme
import com.parkingSystem.parkingSystem.user.home.parking.TopBar
import com.parkingSystem.parkingSystem.viewmodel.NotificationViewModel
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel

@Composable
fun NotificationPage(context: Context, navHostController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val notificationViewModel: NotificationViewModel = viewModel(factory = viewModelFactory {
        initializer { NotificationViewModel(sharedPreferences) }
    })

    val notifications by notificationViewModel.notifications.collectAsState()
    var userId by remember { mutableStateOf("") }
    val gradientTheme = LocalGradientTheme.current

    // Lấy userId và fetch thông báo
    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        if (userId.isNotEmpty()) {
            notificationViewModel.fetchNotificationByUserId(userId)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        if (notifications.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No notifications yet !",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

            }
        } else {
            // Danh sách thông báo
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
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
fun NotificationItem(
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