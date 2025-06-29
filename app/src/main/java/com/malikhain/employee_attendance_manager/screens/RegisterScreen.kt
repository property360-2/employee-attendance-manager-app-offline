package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.viewmodel.AuthViewModel
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
import com.malikhain.employee_attendance_manager.viewmodel.RegistrationState
import com.malikhain.employee_attendance_manager.utils.SecurityUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val registrationState by viewModel.registrationState.collectAsState()

    // Real-time validation
    val usernameValidation = SecurityUtils.validateUsername(username)
    val emailValidation = if (email.isNotBlank()) SecurityUtils.validateEmail(email) else null
    val passwordValidation = if (password.isNotBlank()) SecurityUtils.validatePassword(password) else null
    val passwordsMatch = password == confirmPassword && password.isNotBlank()

    LaunchedEffect(registrationState) {
        when (val state = registrationState) {
            is RegistrationState.Success -> {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
                viewModel.resetRegistrationState()
            }
            is RegistrationState.Error -> {
                // Error is handled in the UI
            }
            RegistrationState.Loading -> {
                // Loading state is handled in the UI
            }
            RegistrationState.Idle -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Register Admin") })
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    "Create Admin Account",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Set up your administrator account",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            item {
                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = username.isNotBlank() && usernameValidation is com.malikhain.employee_attendance_manager.utils.UsernameValidationResult.Error,
                    supportingText = {
                        if (username.isNotBlank() && usernameValidation is com.malikhain.employee_attendance_manager.utils.UsernameValidationResult.Error) {
                            Text(usernameValidation.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }
            
            item {
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = email.isNotBlank() && emailValidation is com.malikhain.employee_attendance_manager.utils.EmailValidationResult.Error,
                    supportingText = {
                        if (email.isNotBlank() && emailValidation is com.malikhain.employee_attendance_manager.utils.EmailValidationResult.Error) {
                            Text(emailValidation.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }
            
            item {
                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Close else Icons.Default.Check,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = password.isNotBlank() && passwordValidation is com.malikhain.employee_attendance_manager.utils.PasswordValidationResult.Error,
                    supportingText = {
                        if (password.isNotBlank() && passwordValidation is com.malikhain.employee_attendance_manager.utils.PasswordValidationResult.Error) {
                            Text(passwordValidation.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }
            
            item {
                // Confirm Password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Close else Icons.Default.Check,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPassword.isNotBlank() && !passwordsMatch,
                    supportingText = {
                        if (confirmPassword.isNotBlank() && !passwordsMatch) {
                            Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }
            
            item {
                // Password requirements
                if (password.isNotBlank()) {
                    PasswordRequirementsCard(password = password)
                }
            }
            
            item {
                Button(
                    onClick = { viewModel.register(username, password, email.takeIf { it.isNotBlank() }) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = username.isNotBlank() && 
                             password.isNotBlank() && 
                             confirmPassword.isNotBlank() &&
                             usernameValidation is com.malikhain.employee_attendance_manager.utils.UsernameValidationResult.Success &&
                             (email.isBlank() || emailValidation is com.malikhain.employee_attendance_manager.utils.EmailValidationResult.Success) &&
                             passwordValidation is com.malikhain.employee_attendance_manager.utils.PasswordValidationResult.Success &&
                             passwordsMatch
                ) {
                    Text("Create Account")
                }
            }
            
            item {
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Already have an account? Login")
                }
            }
            
            item {
                // Registration state messages
                val errorMessage = (registrationState as? RegistrationState.Error)?.message
                if (errorMessage != null) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordRequirementsCard(password: String) {
    val requirements = listOf(
        "At least 8 characters" to (password.length >= 8),
        "One uppercase letter" to password.any { it.isUpperCase() },
        "One lowercase letter" to password.any { it.isLowerCase() },
        "One number" to password.any { it.isDigit() },
        "One special character" to password.any { !it.isLetterOrDigit() }
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Password Requirements",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            requirements.forEach { (requirement, isMet) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isMet) "Requirement met" else "Requirement not met",
                        tint = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        requirement,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isMet) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

// TODO: Add password strength indicator with visual feedback
// TODO: Implement email verification for admin registration
// TODO: Add terms and conditions acceptance checkbox
// TODO: Support for admin role selection (super admin, regular admin, etc.)
// TODO: Add profile picture upload during registration
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
