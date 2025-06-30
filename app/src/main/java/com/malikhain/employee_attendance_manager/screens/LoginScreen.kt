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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var rememberUsername by remember { mutableStateOf(false) }
    
    val loginState by viewModel.loginState.collectAsState()
    val biometricAvailable by viewModel.biometricAvailable.collectAsState()
    val savedUsername by viewModel.savedUsername.collectAsState()
    val rememberUsernameSetting by viewModel.rememberUsername.collectAsState()
    
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Load saved username if available
    LaunchedEffect(savedUsername, rememberUsernameSetting) {
        if (rememberUsernameSetting && savedUsername != null) {
            username = savedUsername!!
            rememberUsername = true
        }
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                viewModel.resetLoginState()
            }
            is LoginState.BiometricRequired -> {
                // Handle biometric authentication
                coroutineScope.launch {
                    val success = com.malikhain.employee_attendance_manager.utils.BiometricUtils.authenticateBiometric(
                        context as androidx.fragment.app.FragmentActivity
                    )
                    if (success) {
                        viewModel.handleBiometricSuccess()
                    } else {
                        viewModel.resetLoginState()
                    }
                }
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
                rememberMe = rememberMe,
                onRememberMeChange = { rememberMe = it },
                rememberUsername = rememberUsername,
                onRememberUsernameChange = { 
                    rememberUsername = it
                    viewModel.setRememberUsername(it)
                },
                onLogin = { viewModel.login(username, password, rememberMe) },
                onBiometricLogin = { viewModel.loginWithBiometric() },
                onRegister = { navController.navigate("register") },
                loginState = loginState,
                biometricAvailable = biometricAvailable
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
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    rememberUsername: Boolean,
    onRememberUsernameChange: (Boolean) -> Unit,
    onLogin: () -> Unit,
    onBiometricLogin: () -> Unit,
    onRegister: () -> Unit,
    loginState: LoginState,
    biometricAvailable: Boolean
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
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = loginState !is LoginState.Loading
            )
            
            // Password strength indicator
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthIndicator(password = password)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Remember options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = onRememberMeChange,
                        enabled = loginState !is LoginState.Loading
                    )
                    Text(
                        "Remember me",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberUsername,
                        onCheckedChange = onRememberUsernameChange,
                        enabled = loginState !is LoginState.Loading
                    )
                    Text(
                        "Remember username",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Login button
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
            
            // Biometric login button
            if (biometricAvailable) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onBiometricLogin,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginState !is LoginState.Loading
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Biometric",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign in with Biometric")
                }
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

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val strengthInfo = when (strength) {
        0 -> Pair(Color.Gray, "Very Weak")
        1 -> Pair(Color.Red, "Weak")
        2 -> Pair(Color(0xFFFF9800), "Fair")
        3 -> Pair(Color.Yellow, "Good")
        4 -> Pair(Color.Green, "Strong")
        else -> Pair(Color.Green, "Very Strong")
    }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Password Strength:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                strengthInfo.second,
                style = MaterialTheme.typography.bodySmall,
                color = strengthInfo.first
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index < strength) strengthInfo.first else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

private fun calculatePasswordStrength(password: String): Int {
    var strength = 0
    
    if (password.length >= 8) strength++
    if (password.any { it.isUpperCase() }) strength++
    if (password.any { it.isLowerCase() }) strength++
    if (password.any { it.isDigit() }) strength++
    if (password.any { !it.isLetterOrDigit() }) strength++
    
    return strength
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
