package com.parkingSystem.parkingSystem.staff

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.parkingSystem.parkingSystem.R
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel

@Composable
fun StaffTopAppBar(
    sharedPreferences: SharedPreferences,
    userViewModel: UserViewModel,
    openDrawer: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    val user by viewModel.user.collectAsState()
    var userName by remember { mutableStateOf("Staff") }
    var role by remember { mutableStateOf("Staff") }
    val gradientTheme = LocalGradientTheme.current

    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
        userName = viewModel.getUserNameFromToken()
        role = viewModel.getUserRole()
    }

    Column(
        modifier = Modifier
            .background(gradientTheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Staff Icon",
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // User greeting
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Hello,",
                        fontSize = 14.sp,
                        color = Color(0xFF002E5D)
                    )
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        color = Color(0xFF002E5D),
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.logout(context) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        tint = Color(0xFF002E5D),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}