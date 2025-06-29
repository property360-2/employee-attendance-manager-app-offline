package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.viewmodel.AuthViewModel
import com.malikhain.employee_attendance_manager.viewmodel.LoginState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                viewModel.resetLoginState()
            }
            is LoginState.Error -> {
                // Error is handled in the UI
            }
            is LoginState.AccountLocked -> {
                // Account locked state is handled in the UI
            }
            else -> { /* Other states handled in UI */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Login") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginContent(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = it },
                onLogin = { viewModel.login(username, password) },
                onRegister = { navController.navigate("register") },
                loginState = loginState
            )
        }
    }
}

@Composable
private fun LoginContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    loginState: LoginState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Sign in to your admin account",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is LoginState.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Close else Icons.Default.Check,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is LoginState.Loading
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is LoginState.Loading && username.isNotBlank() && password.isNotBlank()
            ) {
                if (loginState is LoginState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Sign In")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onRegister) {
                Text("Don't have an account? Register")
            }
            
            // Error and status messages
            when (loginState) {
                is LoginState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        loginState.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is LoginState.AccountLocked -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(loginState.remainingTime)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(loginState.remainingTime) % 60
                    Text(
                        "Account locked. Try again in ${minutes}m ${seconds}s",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> { /* No message */ }
            }
        }
    }
}

// TODO: Add "Remember me/Stay logged in" option with secure token storage
// TODO: Implement biometric authentication (fingerprint/face unlock)
// TODO: Add CAPTCHA for security after multiple failed attempts
// TODO: Show login attempt history and suspicious activity alerts
// TODO: Add "Remember username" functionality
// TODO: Add two-factor authentication (2FA) support
// TODO: Add login analytics and audit logging
// TODO: Support for multiple admin accounts with role-based access
// TODO: Add login notifications for security awareness
// TODO: Support for SSO (Single Sign-On) integration
// TODO: Add device management and login from new device notifications
