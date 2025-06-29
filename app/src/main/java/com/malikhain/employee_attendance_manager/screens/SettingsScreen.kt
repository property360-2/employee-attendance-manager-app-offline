package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.style.TextAlign
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.malikhain.employee_attendance_manager.viewmodel.SettingsViewModel
import com.malikhain.employee_attendance_manager.utils.ExportUtils
import com.malikhain.employee_attendance_manager.viewmodel.ExportState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var biometricAuth by remember { mutableStateOf(false) }
    var autoBackup by remember { mutableStateOf(true) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    
    val viewModel: SettingsViewModel = hiltViewModel()
    val exportState by viewModel.exportState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(exportState) {
        when (val currentState = exportState) {
            is ExportState.Success -> {
                ExportUtils.shareFile(context, currentState.uri, currentState.fileName)
                viewModel.resetExportState()
            }
            is ExportState.Error -> {
                // Show error message
                viewModel.resetExportState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // App Settings Section
            SettingsSection(title = "App Settings") {
                SettingsItem(
                    icon = Icons.Default.Settings,
                    title = "Dark Mode",
                    subtitle = "Toggle dark theme",
                    trailing = {
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Enable push notifications",
                    trailing = {
                        Switch(
                            checked = notifications,
                            onCheckedChange = { notifications = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face unlock",
                    trailing = {
                        Switch(
                            checked = biometricAuth,
                            onCheckedChange = { biometricAuth = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Auto Backup",
                    subtitle = "Automatically backup data",
                    trailing = {
                        Switch(
                            checked = autoBackup,
                            onCheckedChange = { autoBackup = it }
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Data Management Section
            SettingsSection(title = "Data Management") {
                SettingsItem(
                    icon = Icons.Default.Check,
                    title = "Export Employees",
                    subtitle = "Export employee data to CSV",
                    onClick = { viewModel.exportEmployees(context) }
                )
                
                SettingsItem(
                    icon = Icons.Default.Check,
                    title = "Export Attendance",
                    subtitle = "Export attendance data to CSV",
                    onClick = { viewModel.exportAttendance(context) }
                )
                
                SettingsItem(
                    icon = Icons.Default.Close,
                    title = "Import Data",
                    subtitle = "Import data from file",
                    onClick = { /* TODO: Implement import */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Backup Now",
                    subtitle = "Create manual backup",
                    onClick = { viewModel.createBackup() }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Security Section
            SettingsSection(title = "Security") {
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update your admin password",
                    onClick = { navController.navigate("change_password") }
                )
                
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Security Settings",
                    subtitle = "Configure security options",
                    onClick = { /* TODO: Implement security settings */ }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // About Section
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    subtitle = "1.0.0",
                    onClick = { /* Show version info */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Help & Support",
                    subtitle = "Get help and contact support",
                    onClick = { /* TODO: Show help */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Privacy Policy",
                    subtitle = "Read our privacy policy",
                    onClick = { /* TODO: Show privacy policy */ }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Danger Zone
            SettingsSection(title = "Danger Zone") {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Clear All Data",
                    subtitle = "Permanently delete all data",
                    onClick = { showConfirmDialog = true },
                    titleColor = MaterialTheme.colorScheme.error
                )
                
                SettingsItem(
                    icon = Icons.Default.Close,
                    title = "Logout",
                    subtitle = "Sign out of your account",
                    onClick = { 
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    titleColor = MaterialTheme.colorScheme.error
                )
            }
        }
        
        // Export Loading Dialog
        if (exportState is ExportState.Loading) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Exporting Data") },
                text = { 
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Please wait while we prepare your data for export...")
                    }
                },
                confirmButton = { }
            )
        }
        
        // Clear Data Confirmation Dialog
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Clear All Data") },
                text = { 
                    Text(
                        "This action will permanently delete all employee and attendance data. This cannot be undone.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            viewModel.clearAllData()
                        }
                    ) {
                        Text("Clear Data", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick?.invoke() },
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            trailing?.invoke()
        }
    }
} 