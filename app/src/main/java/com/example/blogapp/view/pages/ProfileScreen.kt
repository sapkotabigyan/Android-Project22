package com.example.blogapp.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import com.example.blogapp.view.LoginActivity
import com.example.blogapp.repository.UserRepositoryImpl
import com.example.blogapp.viewModel.UserViewModel

@Composable
fun ProfileScreen(
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {}
) {
    val context = LocalContext.current
    val userRepo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(userRepo) }
    val currentUser = userViewModel.getCurrentUser()
    
    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf(currentUser?.email ?: "user@example.com") }

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { uid ->
            userViewModel.getUserFromDatabase(uid) { success, _, userModel ->
                if (success && userModel != null) {
                    userName = "${userModel.firstName} ${userModel.lastName}"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Custom Header to match image
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Craft Work Nepal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513)
            )
            IconButton(onClick = {
                userViewModel.logout { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color(0xFF8B4513))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture Circle with Initials
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF8B4513), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (userName.length >= 2) userName.take(2).uppercase() else "U",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Profile Info
            Column {
                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Statistics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStatItem("Orders", "12", onNavigateToOrders)
            ProfileStatItem("Wishlist", "8", onNavigateToWishlist)
            ProfileStatItem("Reviews", "5") {}
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(thickness = 4.dp, color = Color(0xFFE0E0E0))
        Spacer(modifier = Modifier.height(16.dp))
        
        // Menu Items matching the provided image
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(getProfileMenuItems()) { item ->
                ProfileMenuItemRow(item = item) {
                    when (item.title) {
                        "Wishlist" -> onNavigateToWishlist()
                        "Logout" -> {
                            userViewModel.logout { _, _ ->
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        }
                        "My Orders" -> onNavigateToOrders()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, value: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileMenuItemRow(item: ProfileMenuData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

data class ProfileMenuData(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color
)

fun getProfileMenuItems() = listOf(
    ProfileMenuData("Wishlist", Icons.Default.Favorite, Color(0xFFE91E63)),
    ProfileMenuData("Addresses", Icons.Default.LocationOn, Color(0xFF2196F3)),
    ProfileMenuData("Payment Methods", Icons.Default.ThumbUp, Color(0xFF4CAF50)),
    ProfileMenuData("Settings", Icons.Default.Settings, Color(0xFF9E9E9E)),
    ProfileMenuData("Help & Support", Icons.Default.Home, Color(0xFF607D8B)),
    ProfileMenuData("About", Icons.Default.Info, Color(0xFF795548)),
    ProfileMenuData("Logout", Icons.Default.ExitToApp, Color(0xFFF44336))
)
