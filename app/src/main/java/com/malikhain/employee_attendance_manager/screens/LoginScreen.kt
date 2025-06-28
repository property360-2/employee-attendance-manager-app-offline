package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.viewmodel.AuthViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState == true) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        } else if (loginState == false) {
            error = "Invalid username or password"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Admin Login") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Admin Login", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (username.isBlank() || password.isBlank()) {
                    error = "Please enter both username and password"
                } else {
                    error = null
                    viewModel.login(username, password)
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Login")
            }
            TextButton(onClick = { navController.navigate("register") }) {
                Text("Register")
            }
            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// TODO: Add password strength requirements and validation
// TODO: Implement "Forgot Password" functionality with email recovery
// TODO: Add "Remember me/Stay logged in" option with secure token storage
// TODO: Implement biometric authentication (fingerprint/face unlock)
// TODO: Add rate limiting for failed login attempts to prevent brute force attacks
// TODO: Add password visibility toggle for better UX
// TODO: Show loading state during authentication process
// TODO: Add input validation with real-time feedback (username format, password requirements)
// TODO: Implement proper password hashing (currently plain text - use bcrypt/Argon2)
// TODO: Add CAPTCHA for security after multiple failed attempts
// TODO: Show login attempt history and suspicious activity alerts
// TODO: Add "Remember username" functionality
// TODO: Implement proper session management with token expiration
// TODO: Add logout functionality with session cleanup
// TODO: Add two-factor authentication (2FA) support
// TODO: Implement account lockout after multiple failed attempts
// TODO: Add login analytics and audit logging
// TODO: Support for multiple admin accounts with role-based access
// TODO: Add password change functionality
// TODO: Implement secure password reset flow
// TODO: Add login notifications for security awareness
// TODO: Support for SSO (Single Sign-On) integration
// TODO: Add device management and login from new device notifications
