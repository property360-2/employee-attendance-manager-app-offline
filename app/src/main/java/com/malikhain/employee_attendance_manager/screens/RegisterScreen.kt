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
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import com.malikhain.employee_attendance_manager.viewmodel.RegistrationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val registrationState by viewModel.registrationState.collectAsState()

    LaunchedEffect(registrationState) {
        when (val state = registrationState) {
            is RegistrationState.Success -> {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
                viewModel.resetRegistrationState()
            }
            is RegistrationState.Error -> {
                error = state.message
                viewModel.resetRegistrationState()
            }
            RegistrationState.Idle -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Register Admin") })
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
            Text("Register Admin", style = MaterialTheme.typography.titleLarge)
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
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                when {
                    username.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                        error = "All fields are required"
                    }
                    password != confirmPassword -> {
                        error = "Passwords do not match"
                    }
                    else -> {
                        error = null
                        viewModel.register(username, password)
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Register")
            }
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Back to Login")
            }
            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// TODO: Add password strength indicator with visual feedback
// TODO: Implement email verification for admin registration
// TODO: Add terms and conditions acceptance checkbox
// TODO: Support for admin role selection (super admin, regular admin, etc.)
// TODO: Add profile picture upload during registration
// TODO: Implement password confirmation validation with real-time feedback
// TODO: Add password requirements display and validation
// TODO: Implement email validation with proper regex patterns
// TODO: Add admin approval workflow for new registrations
// TODO: Show password strength meter with color coding
// TODO: Add profile setup wizard after successful registration
// TODO: Implement username availability check in real-time
// TODO: Add registration confirmation email
// TODO: Support for organization/company details during registration
// TODO: Add admin invitation system for multi-admin setups
// TODO: Implement registration analytics and tracking
// TODO: Add CAPTCHA for bot prevention during registration
// TODO: Support for social login integration (Google, Microsoft)
// TODO: Add registration success page with next steps
// TODO: Implement admin onboarding tutorial after registration
// TODO: Add data privacy and GDPR compliance notices
// TODO: Support for custom admin permissions and roles
// TODO: Add registration audit trail for security
// TODO: Implement admin profile completion reminders
