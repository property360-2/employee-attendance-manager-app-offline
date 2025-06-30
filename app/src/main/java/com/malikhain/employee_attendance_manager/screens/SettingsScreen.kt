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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.malikhain.employee_attendance_manager.navigation.Screen
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeWithAttendance
import com.malikhain.employee_attendance_manager.screens.components.DashboardStats
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showDataRetentionDialog by remember { mutableStateOf(false) }
    
    val viewModel: SettingsViewModel = hiltViewModel()
    val exportState by viewModel.exportState.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // File picker launcher for import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            scope.launch {
                try {
                    // TODO: Implement actual import logic based on file type
                    snackbarHostState.showSnackbar("File selected: ${selectedUri.lastPathSegment}")
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Import failed: ${e.message}")
                }
            }
        }
    }

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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                        subtitle = "Current: ${themeMode.replaceFirstChar { it.uppercase() }}",
                        onClick = { showThemeDialog = true }
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
                }
            }
            
            // Help & Support Section
            item {
                SettingsSection(title = "Help & Support") {
                    SettingsItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        subtitle = "Contact support team for assistance",
                        onClick = { navController.navigate("help_support") }
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
                        icon = Icons.Default.Info,
                        title = "Privacy Policy",
                        subtitle = "Read our privacy policy",
                        onClick = { navController.navigate("privacy_policy") }
                    )
                    
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        subtitle = "Read our terms of service",
                        onClick = { navController.navigate("terms_and_conditions") }
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
                currentTheme = themeMode,
                onDismiss = { showThemeDialog = false },
                onThemeSelected = { theme ->
                    viewModel.setThemeMode(theme)
                    showThemeDialog = false
                }
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
                onImport = { importType ->
                    when (importType) {
                        "employees" -> filePickerLauncher.launch("text/csv")
                        "attendance" -> filePickerLauncher.launch("text/csv")
                        "all" -> filePickerLauncher.launch("application/json")
                        else -> filePickerLauncher.launch("*/*")
                    }
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
    currentTheme: String,
    onDismiss: () -> Unit,
    onThemeSelected: (String) -> Unit
) {
    val themes = listOf("System", "Light", "Dark")
    var selectedTheme by remember { 
        mutableStateOf(currentTheme.replaceFirstChar { it.uppercase() })
    }
    
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
            TextButton(onClick = { 
                onThemeSelected(selectedTheme.lowercase())
            }) {
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