package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextAlign
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.malikhain.employee_attendance_manager.viewmodel.SettingsViewModel
import com.malikhain.employee_attendance_manager.utils.ExportUtils
import com.malikhain.employee_attendance_manager.viewmodel.ExportState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var biometricAuth by remember { mutableStateOf(false) }
    var autoBackup by remember { mutableStateOf(true) }
    var cloudBackup by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNotificationSettings by remember { mutableStateOf(false) }
    var showSecuritySettings by remember { mutableStateOf(false) }
    var showDataRetentionDialog by remember { mutableStateOf(false) }
    
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Settings Section
            item {
                SettingsSection(title = "App Settings") {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = "Choose your preferred theme",
                        onClick = { showThemeDialog = true }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = "Select your language",
                        onClick = { showLanguageDialog = true }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Configure notification preferences",
                        onClick = { showNotificationSettings = true }
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
                }
            }
            
            // Data Management Section
            item {
                SettingsSection(title = "Data Management") {
                    SettingsItem(
                        icon = Icons.Default.Upload,
                        title = "Import Data",
                        subtitle = "Import data from CSV file",
                        onClick = { showImportDialog = true }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Save,
                        title = "Export Data",
                        subtitle = "Export data in various formats",
                        onClick = { showExportDialog = true }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Upload,
                        title = "Cloud Backup",
                        subtitle = "Backup data to cloud storage",
                        trailing = {
                            Switch(
                                checked = cloudBackup,
                                onCheckedChange = { cloudBackup = it }
                            )
                        }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Save,
                        title = "Auto Backup",
                        subtitle = "Automatically backup data",
                        trailing = {
                            Switch(
                                checked = autoBackup,
                                onCheckedChange = { autoBackup = it }
                            )
                        }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.AccessTime,
                        title = "Data Retention",
                        subtitle = "Configure data retention policies",
                        onClick = { showDataRetentionDialog = true }
                    )
                }
            }
            
            // Security Section
            item {
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
                        subtitle = "Configure advanced security options",
                        onClick = { showSecuritySettings = true }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.History,
                        title = "Login History",
                        subtitle = "View recent login activity",
                        onClick = { /* TODO: Show login history */ }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "Device Management",
                        subtitle = "Manage connected devices",
                        onClick = { /* TODO: Show device management */ }
                    )
                }
            }
            
            // About Section
            item {
                SettingsSection(title = "About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "App Version",
                        subtitle = "1.0.0",
                        onClick = { /* Show version info */ }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Help,
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
                    
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        subtitle = "Read our terms of service",
                        onClick = { /* TODO: Show terms */ }
                    )
                }
            }
            
            // Danger Zone
            item {
                SettingsSection(title = "Danger Zone") {
                    SettingsItem(
                        icon = Icons.Default.Delete,
                        title = "Clear All Data",
                        subtitle = "Permanently delete all data",
                        onClick = { showConfirmDialog = true },
                        titleColor = MaterialTheme.colorScheme.error
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.ExitToApp,
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
        }
        
        // Theme Selection Dialog
        if (showThemeDialog) {
            ThemeSelectionDialog(
                onDismiss = { showThemeDialog = false },
                onThemeSelected = { theme ->
                    // TODO: Implement theme change
                    showThemeDialog = false
                }
            )
        }
        
        // Language Selection Dialog
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { language ->
                    // TODO: Implement language change
                    showLanguageDialog = false
                }
            )
        }
        
        // Notification Settings Dialog
        if (showNotificationSettings) {
            NotificationSettingsDialog(
                onDismiss = { showNotificationSettings = false }
            )
        }
        
        // Security Settings Dialog
        if (showSecuritySettings) {
            SecuritySettingsDialog(
                onDismiss = { showSecuritySettings = false }
            )
        }
        
        // Data Retention Dialog
        if (showDataRetentionDialog) {
            DataRetentionDialog(
                onDismiss = { showDataRetentionDialog = false }
            )
        }
        
        // Import Dialog
        if (showImportDialog) {
            ImportDataDialog(
                onDismiss = { showImportDialog = false },
                onImport = { fileUri ->
                    viewModel.importData(fileUri)
                    showImportDialog = false
                }
            )
        }
        
        // Export Dialog
        if (showExportDialog) {
            ExportDataDialog(
                onDismiss = { showExportDialog = false },
                onExport = { format ->
                    when (format) {
                        "employees" -> viewModel.exportEmployees(context)
                        "attendance" -> viewModel.exportAttendance(context)
                        "all" -> viewModel.exportAllData(context)
                    }
                    showExportDialog = false
                }
            )
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

@Composable
private fun ThemeSelectionDialog(
    onDismiss: () -> Unit,
    onThemeSelected: (String) -> Unit
) {
    val themes = listOf("System", "Light", "Dark")
    var selectedTheme by remember { mutableStateOf("System") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme") },
        text = {
            Column {
                themes.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == theme,
                            onClick = { selectedTheme = theme }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(theme)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onThemeSelected(selectedTheme) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("English", "Spanish", "French", "German", "Chinese")
    var selectedLanguage by remember { mutableStateOf("English") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                languages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == language,
                            onClick = { selectedLanguage = language }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(language)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onLanguageSelected(selectedLanguage) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NotificationSettingsDialog(
    onDismiss: () -> Unit
) {
    var attendanceReminders by remember { mutableStateOf(true) }
    var dailyReports by remember { mutableStateOf(false) }
    var weeklyReports by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notification Settings") },
        text = {
            Column {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Attendance Reminders",
                    subtitle = "Daily reminders to mark attendance",
                    trailing = {
                        Switch(
                            checked = attendanceReminders,
                            onCheckedChange = { attendanceReminders = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Daily Reports",
                    subtitle = "Receive daily attendance summaries",
                    trailing = {
                        Switch(
                            checked = dailyReports,
                            onCheckedChange = { dailyReports = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Weekly Reports",
                    subtitle = "Receive weekly attendance reports",
                    trailing = {
                        Switch(
                            checked = weeklyReports,
                            onCheckedChange = { weeklyReports = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.VolumeUp,
                    title = "Sound",
                    subtitle = "Play sound for notifications",
                    trailing = {
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { soundEnabled = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Vibration,
                    title = "Vibration",
                    subtitle = "Vibrate for notifications",
                    trailing = {
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it }
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SecuritySettingsDialog(
    onDismiss: () -> Unit
) {
    var sessionTimeout by remember { mutableStateOf(30) }
    var requireBiometric by remember { mutableStateOf(true) }
    var auditLogging by remember { mutableStateOf(true) }
    var dataEncryption by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Security Settings") },
        text = {
            Column {
                SettingsItem(
                    icon = Icons.Default.AccessTime,
                    title = "Backup Schedule",
                    subtitle = "Configure automatic backup schedule"
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Data Analytics",
                    subtitle = "View data usage and analytics"
                )
                
                SettingsItem(
                    icon = Icons.Default.Analytics,
                    title = "Performance",
                    subtitle = "Monitor app performance"
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Sound",
                    subtitle = "Configure notification sounds"
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Vibration",
                    subtitle = "Configure vibration patterns"
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Auto Lock",
                    subtitle = "Configure automatic lock timer"
                )
                
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Biometric",
                    subtitle = "Configure biometric authentication"
                )
                
                SettingsItem(
                    icon = Icons.Default.History,
                    title = "Audit Log",
                    subtitle = "View security audit logs"
                )
                
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Encryption",
                    subtitle = "Configure data encryption"
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Session Timeout",
                    subtitle = "Configure session timeout"
                )
                
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Clear Cache",
                    subtitle = "Clear app cache and temporary files"
                )
                
                SettingsItem(
                    icon = Icons.Default.Save,
                    title = "Backup Now",
                    subtitle = "Create manual backup"
                )
                
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "User Management",
                    subtitle = "Manage user accounts and permissions"
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Time Tracking",
                    subtitle = "Configure time tracking settings"
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Data Usage",
                    subtitle = "Monitor data usage and limits"
                )
                
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Team Management",
                    subtitle = "Manage team members and roles"
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Work Schedule",
                    subtitle = "Configure work schedules"
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Storage",
                    subtitle = "Manage storage and data usage"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DataRetentionDialog(
    onDismiss: () -> Unit
) {
    var retentionPeriod by remember { mutableStateOf(365) }
    var autoDelete by remember { mutableStateOf(true) }
    var backupBeforeDelete by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Data Retention Policy") },
        text = {
            Column {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Retention Period",
                    subtitle = "Keep data for ${retentionPeriod} days",
                    onClick = { /* TODO: Show period picker */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Auto Delete",
                    subtitle = "Automatically delete old data",
                    trailing = {
                        Switch(
                            checked = autoDelete,
                            onCheckedChange = { autoDelete = it }
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Save,
                    title = "Backup Before Delete",
                    subtitle = "Create backup before deleting old data",
                    trailing = {
                        Switch(
                            checked = backupBeforeDelete,
                            onCheckedChange = { backupBeforeDelete = it }
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ImportDataDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Data") },
        text = {
            Column {
                Text("Select the type of data to import:")
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Employees",
                    subtitle = "Import employee data from CSV",
                    onClick = { onImport("employees") }
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Attendance",
                    subtitle = "Import attendance data from CSV",
                    onClick = { onImport("attendance") }
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "All Data",
                    subtitle = "Import all data from backup file",
                    onClick = { onImport("all") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ExportDataDialog(
    onDismiss: () -> Unit,
    onExport: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Data") },
        text = {
            Column {
                Text("Select the data to export:")
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Employees",
                    subtitle = "Export employee data to CSV",
                    onClick = { onExport("employees") }
                )
                
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Attendance",
                    subtitle = "Export attendance data to CSV",
                    onClick = { onExport("attendance") }
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "All Data",
                    subtitle = "Export all data to backup file",
                    onClick = { onExport("all") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 