package com.parkingSystem.parkingSystem.user.personal

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.auth0.android.jwt.JWT
import com.parkingSystem.parkingSystem.requestmodel.UpdateUserInput
import com.parkingSystem.parkingSystem.responsemodel.User
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel
import com.parkingSystem.parkingSystem.R
@Composable
fun EditUserProfile(sharedPreferences: SharedPreferences, navHostController: NavHostController) {
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val user = userViewModel.user.collectAsState()

    if (user == null) return

    // Sử dụng ?: để cung cấp giá trị mặc định khi null
    var avatarURL by remember { mutableStateOf<Uri?>(null) }
    var nameText by remember { mutableStateOf(user.value?.name ?: "") }
    var emailText by remember { mutableStateOf(user.value?.email ?: "") }
    var phoneText by remember { mutableStateOf(user.value?.phone ?: "") }
    var addressText by remember { mutableStateOf(user.value?.address ?: "") }
    var passwordText by remember { mutableStateOf("") }
    var repasswordText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { HeadbarEditUserProfile(navHostController) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item {
                ContentEditUser(
                    nameText, { nameText = it },
                    emailText, { emailText = it },
                    phoneText, { phoneText = it },
                    addressText, { addressText = it },
                    passwordText, { passwordText = it },
                    repasswordText, { repasswordText = it },
                )
            }
            item {
                user?.let {
                    AcceptEditButton(
                        userId = it.value?.uid.toString(),
                        nameText = nameText,
                        emailText = emailText,
                        phoneText = phoneText,
                        addressText = addressText,
                        passwordText = passwordText,
                        repasswordText = repasswordText,
                        avatarURL = avatarURL,
                        role = it.value?.role ?: "User",
                        viewModel = userViewModel,
                        navHostController = navHostController
                    )
                }
            }
        }
    }
}

@Composable
fun HeadbarEditUserProfile(navHostController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { navHostController.popBackStack() }
        )
        Text(
            text = "Edit User Profile",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//@Composable
//fun ChangeAvatar(
//    user: User,
//    imageUri: Uri?,
//    onImageSelected: (Uri) -> Unit
//) {
//    val context = LocalContext.current
//    var showPermissionDialog by remember { mutableStateOf(false) }
//
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { onImageSelected(it) }
//    }
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            imagePickerLauncher.launch("image/*")
//        } else {
//            showPermissionDialog = true
//        }
//    }
//
//    fun checkAndRequestPermission() {
//        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Manifest.permission.READ_MEDIA_IMAGES
//        } else {
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        }
//
//        when {
//            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
//                imagePickerLauncher.launch("image/*")
//            }
//            else -> {
//                permissionLauncher.launch(permission)
//            }
//        }
//    }
//
//    // UI cho avatar (bạn cần thêm phần hiển thị avatar và button để chọn ảnh)
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Hiển thị avatar hiện tại hoặc ảnh đã chọn
//        Box(
//            modifier = Modifier
//                .size(120.dp)
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.surfaceVariant)
//                .clickable { checkAndRequestPermission() }
//        ) {
//            // Load avatar từ URL hoặc URI
//            // Sử dụng Coil hoặc Glide để load image
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        TextButton(onClick = { checkAndRequestPermission() }) {
//            Text("Thay đổi ảnh đại diện")
//        }
//    }
//
//    if (showPermissionDialog) {
//        AlertDialog(
//            onDismissRequest = { showPermissionDialog = false },
//            title = { Text("Cần quyền truy cập") },
//            text = { Text("Ứng dụng cần quyền truy cập thư viện ảnh để thay đổi ảnh đại diện") },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        showPermissionDialog = false
//                        openAppSettings(context)
//                    }
//                ) {
//                    Text("Đồng ý")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showPermissionDialog = false }) {
//                    Text("Hủy")
//                }
//            }
//        )
//    }
//}

private fun openAppSettings(context: android.content.Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Composable
fun ContentEditUser(
    nameText: String, onNameChange: (String) -> Unit,
    emailText: String, onEmailChange: (String) -> Unit,
    phoneText: String, onPhoneChange: (String) -> Unit,
    addressText: String, onAddressChange: (String) -> Unit,
    passwordText: String, onPasswordChange: (String) -> Unit,
    repasswordText: String, onRepasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InputEditField("Full name", nameText, onNameChange, "Input name")
        InputEditField("Email", emailText, onEmailChange, "Input email")
        InputEditField("Phone", phoneText, onPhoneChange, "Input phone")
        InputEditField("Address", addressText, onAddressChange, "Input address")
        InputEditField("Password", passwordText, onPasswordChange, "Input password", true)
        InputEditField("Re-password", repasswordText, onRepasswordChange, "Re-input password", true)
    }
}

@Composable
fun AcceptEditButton(
    userId: String,
    nameText: String,
    emailText: String,
    phoneText: String,
    addressText: String,
    passwordText: String,
    repasswordText: String,
    avatarURL: Uri?,
    role: String,
    viewModel: UserViewModel,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()

    LaunchedEffect(updateSuccess) {
        if (updateSuccess == true) {
            navHostController.navigate("personal")
            viewModel.resetUpdateStatus()
        }
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        enabled = !isUpdating,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        onClick = {
            // Kiểm tra nếu người dùng muốn đổi mật khẩu
            if (passwordText.isNotEmpty() && passwordText != repasswordText) {
                Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
            } else {
                val updateUser = UpdateUserInput(
                    name = nameText,
                    email = emailText,
                    phone = phoneText,
                    address = addressText,
                    role = role,
                    password = if (passwordText.isNotEmpty()) passwordText else null
                )
                viewModel.updateUser(userId, updateUser, context)
            }
        }
    ) {
        if (isUpdating) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text("Đang lưu...")
        } else {
            Text("Lưu thay đổi")
        }
    }
}

@Composable
fun InputEditField(
    nameField: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = nameField,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}