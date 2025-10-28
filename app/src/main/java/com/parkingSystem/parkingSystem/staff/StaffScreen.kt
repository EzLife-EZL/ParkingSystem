package com.parkingSystem.parkingSystem.staff

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.parkingSystem.core.common.activity.BaseActivity
import com.parkingSystem.parkingSystem.ui.theme.ParkingSystemTheme
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel
import com.parkingSystem.parkingSystem.viewmodel.StaffViewModel
import kotlinx.coroutines.launch

class StaffActivityScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

            ParkingSystemTheme {
                val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
                    initializer { UserViewModel(sharedPreferences) }
                })
                val staffViewModel: StaffViewModel = viewModel()

                StaffMainScreen(sharedPreferences, userViewModel, staffViewModel)
            }
        }
    }
}

@Composable
fun GetFcmInstance(sharedPreferences: SharedPreferences, userViewModel: UserViewModel) {
    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
                val userId = userViewModel.getUserAttributeString("userId")
                userViewModel.sendFcmToken(userId.toString(), token)

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMainScreen(
    sharedPreferences: SharedPreferences,
    userViewModel: UserViewModel,
    staffViewModel: StaffViewModel
) {
    GetFcmInstance(sharedPreferences, userViewModel)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Define which routes show top/bottom bars
    val showTopBars = currentRoute in listOf("home", "setting", "notification", "history")
    val showFootBars = currentRoute in listOf("home", "history", "notification", "setting")

    Scaffold(
        topBar = {
            if (showTopBars) {
                StaffTopAppBar(
                    sharedPreferences = sharedPreferences,
                    userViewModel = userViewModel,
                    openDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showFootBars) {
                StaffBottomBar(currentRoute, navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                StaffHomeScreen(navController, userViewModel, staffViewModel)
            }
            composable("history") {
                StaffHistoryScreen(staffViewModel)
            }
            composable("notification") {
                StaffNotificationScreen(context, navController)
            }
            composable("setting") {
                StaffSettingScreen(navController, sharedPreferences)
            }
            composable("qr_scanner") {
                StaffScanQrScreen(
                    onBookingFound = { bookingId ->
                        userViewModel.checkBookingInFirestore(bookingId) { exists ->
                            if (exists) {
                                Toast.makeText(context, "✅ Valid booking!", Toast.LENGTH_SHORT).show()
                            } else {
                                //Toast.makeText(context, "❌ Không tìm thấy đặt chỗ!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                )
            }
            composable("booking_management") {
                BookingManagementScreen(staffViewModel)
            }
        }
    }
}