package com.parkingSystem.parkingSystem.staff

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.parkingSystem.parkingSystem.user.home.booking.BookingDetailScreen
import com.parkingSystem.parkingSystem.user.home.parking.ParkingSlot
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel
import com.parkingSystem.parkingSystem.viewmodel.StaffViewModel
import kotlinx.coroutines.launch

class StaffActivityScreen : BaseActivity() {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
        private const val ALL_PERMISSIONS_REQUEST_CODE = 1003
    }

    /**
     * Kiểm tra và yêu cầu tất cả quyền cần thiết
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Kiểm tra quyền camera
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        // Kiểm tra quyền thông báo (chỉ Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Yêu cầu tất cả quyền còn thiếu
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSIONS_REQUEST_CODE
            )
        } else {
            Log.d("Permission", "All permissions already granted")
        }
    }

    /**
     * Kiểm tra và yêu cầu quyền camera
     */
    private fun checkAndRequestCameraPermission() {
        val permission = Manifest.permission.CAMERA

        when {
            // Đã có quyền
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Permission", "Camera permission already granted")
            }

            // Cần giải thích tại sao cần quyền
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
            ) -> {
                Log.d("Permission", "Should show camera permission rationale")
                requestCameraPermission()
            }

            // Yêu cầu quyền trực tiếp
            else -> {
                requestCameraPermission()
            }
        }
    }

    /**
     * Kiểm tra và yêu cầu quyền thông báo
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestNotificationPermission() {
        val permission = Manifest.permission.POST_NOTIFICATIONS

        when {
            // Đã có quyền
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Permission", "Notification permission already granted")
            }

            // Cần giải thích tại sao cần quyền
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
            ) -> {
                Log.d("Permission", "Should show notification permission rationale")
                requestNotificationPermission()
            }

            // Yêu cầu quyền trực tiếp
            else -> {
                requestNotificationPermission()
            }
        }
    }

    /**
     * Request camera permission
     */
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Request notification permission
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Xử lý kết quả yêu cầu quyền
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "✓ Camera permission granted")
                } else {
                    Log.d("Permission", "✗ Camera permission denied")
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA
                        )) {
                        Log.d("Permission", "Camera: User selected 'Don't ask again'")
                    }
                }
            }

            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "✓ Notification permission granted")
                } else {
                    Log.d("Permission", "✗ Notification permission denied")
                }
            }

            ALL_PERMISSIONS_REQUEST_CODE -> {
                // Xử lý kết quả khi request nhiều quyền cùng lúc
                permissions.forEachIndexed { index, permission ->
                    val isGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED
                    when (permission) {
                        Manifest.permission.CAMERA -> {
                            Log.d("Permission", "Camera: ${if (isGranted) "✓ Granted" else "✗ Denied"}")
                        }
                        Manifest.permission.POST_NOTIFICATIONS -> {
                            Log.d("Permission", "Notification: ${if (isGranted) "✓ Granted" else "✗ Denied"}")
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Yêu cầu tất cả quyền cần thiết khi khởi động
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestAllPermissions()
        } else {
            // Với Android < 13, chỉ cần quyền camera
            checkAndRequestCameraPermission()
        }

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

    /**
     * Kiểm tra xem có quyền camera không
     */
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Kiểm tra xem có quyền thông báo không
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android < 13 không cần runtime permission cho notification
            true
        }
    }

    /**
     * Kiểm tra xem có tất cả quyền cần thiết không
     */
    fun hasAllPermissions(): Boolean {
        return hasCameraPermission() && hasNotificationPermission()
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                                    Toast.makeText(context, "✅ Valid booking!", Toast.LENGTH_SHORT)
                                        .show()
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

                composable("park/{parkId}") { backStackEntry ->
                    val parkId = backStackEntry.arguments?.getString("parkId") ?: ""
                    StaffParkingSlot(context, navController, parkId)
                }
                composable("booking_detail/{bookingId}") { backStackEntry ->
                    val bookingIdArg = backStackEntry.arguments?.getString("bookingId") ?: ""
                    BookingDetailScreen(
                        sharedPreferences = sharedPreferences,
                        navHostController = navController,
                        bookingId = bookingIdArg
                    )
                }
            }
        }

    }
}