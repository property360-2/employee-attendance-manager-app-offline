package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.navigation.Screen
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeWithAttendance
import com.malikhain.employee_attendance_manager.screens.components.DashboardStats
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import com.malikhain.employee_attendance_manager.utils.ExportUtils
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: EmployeeViewModel = hiltViewModel()
    val employees by viewModel.employees.collectAsState()
    val employeesWithAttendance by viewModel.employeesWithAttendance.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showBulkMarkingDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Present") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showQuickActions by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val filters = listOf("All", "Present", "Absent", "Leave", "Not Marked")
    
    // Permission launcher for storage access
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted || android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Permission granted or not needed (Android 10+), proceed with export
            scope.launch {
                isExporting = true
                try {
                    // Get attendance data from ViewModel
                    val attendance = viewModel.getAllAttendance()
                    val uri = ExportUtils.exportToExcel(context, employees, attendance)
                    if (uri != null) {
                        ExportUtils.shareFile(context, uri, "attendance_report.csv")
                        snackbarHostState.showSnackbar("Data exported successfully!")
                    } else {
                        snackbarHostState.showSnackbar("Failed to export data")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Export failed: ${e.message}")
                } finally {
                    isExporting = false
                }
            }
        } else {
            // Permission denied
            scope.launch {
                snackbarHostState.showSnackbar("Storage permission required to export data")
            }
        }
    }
    
    // Function to check if we have storage permission
    fun hasStoragePermission(): Boolean {
        // For modern Android versions (API 29+), we don't need storage permission for app's cache directory
        // For older versions, we'll use WRITE_EXTERNAL_STORAGE
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10+ (API 29+) - no permission needed for app's cache directory
            true
        } else {
            // Android 9 and below - need WRITE_EXTERNAL_STORAGE
            context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    // Function to request appropriate permission
    fun requestStoragePermission() {
        // Only request permission for Android 9 and below
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadEmployees()
    }
    
    LaunchedEffect(searchQuery) {
        viewModel.searchEmployees(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Report") },
                actions = {
                    IconButton(onClick = { navController.navigate("analytics") }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Analytics")
                    }
                    IconButton(onClick = { showQuickActions = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Quick Actions")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Today's Summary Cards
            TodaySummaryCards(employeesWithAttendance)
            
            // Quick Filters
            QuickFilters(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
            
            // Advanced Search Bar
            AdvancedSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showQuickActions = true }
            )
            
            // Employee List with Enhanced UI
            EnhancedEmployeeList(
                employees = employees,
                employeesWithAttendance = employeesWithAttendance,
                selectedFilter = selectedFilter,
                onEmployeeClick = { employee -> navController.navigate("details/${employee.id}") },
                onMarkAttendance = { employeeId, status -> viewModel.markAttendance(employeeId, status) }
            )
        }
        
        // Quick Actions Menu
        if (showQuickActions) {
            QuickActionsMenu(
                onDismiss = { showQuickActions = false },
                onBulkMark = { showBulkMarkingDialog = true },
                onExport = { 
                    // Check permission first
                    when {
                        hasStoragePermission() -> {
                            // Permission already granted or not needed, proceed with export
                            scope.launch {
                                isExporting = true
                                try {
                                    // Get attendance data from ViewModel
                                    val attendance = viewModel.getAllAttendance()
                                    val uri = ExportUtils.exportToExcel(context, employees, attendance)
                                    if (uri != null) {
                                        ExportUtils.shareFile(context, uri, "attendance_report.csv")
                                        snackbarHostState.showSnackbar("Data exported successfully!")
                                    } else {
                                        snackbarHostState.showSnackbar("Failed to export data")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Export failed: ${e.message}")
                                } finally {
                                    isExporting = false
                                }
                            }
                        }
                        else -> {
                            // Request permission (only for Android 9 and below)
                            requestStoragePermission()
                        }
                    }
                    showQuickActions = false
                },
                onRefresh = { viewModel.loadEmployees() }
            )
        }
        
        // Bulk Attendance Marking Dialog
        if (showBulkMarkingDialog) {
            BulkAttendanceDialog(
                employees = employees,
                selectedStatus = selectedStatus,
                onStatusChange = { selectedStatus = it },
                onConfirm = {
                    employees.forEach { employee ->
                        viewModel.markAttendance(employee.id, selectedStatus)
                    }
                    showBulkMarkingDialog = false
                },
                onDismiss = { showBulkMarkingDialog = false }
            )
        }
        
        // Permission Request Dialog
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = { Text("Permission Required") },
                text = { 
                    Text(
                        "This app needs storage permission to export data to your device. " +
                        "This permission is only required on older Android versions (Android 9 and below)."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPermissionDialog = false
                            requestStoragePermission()
                        }
                    ) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Export Loading Dialog
        if (isExporting) {
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
    }
}

@Composable
private fun TodaySummaryCards(employeesWithAttendance: List<EmployeeWithAttendance>) {
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd"))
    val presentCount = employeesWithAttendance.count { it.attendanceStatus == "Present" }
    val absentCount = employeesWithAttendance.count { it.attendanceStatus == "Absent" }
    val leaveCount = employeesWithAttendance.count { it.attendanceStatus == "Leave" }
    val notMarkedCount = employeesWithAttendance.count { it.attendanceStatus == null }
    val totalCount = employeesWithAttendance.size
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SummaryCard(
                title = "Total",
                count = totalCount,
                color = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.People
            )
        }
        item {
            SummaryCard(
                title = "Present",
                count = presentCount,
                color = Color(0xFF4CAF50),
                icon = Icons.Default.CheckCircle
            )
        }
        item {
            SummaryCard(
                title = "Absent",
                count = absentCount,
                color = Color(0xFFF44336),
                icon = Icons.Default.Close
            )
        }
        item {
            SummaryCard(
                title = "Leave",
                count = leaveCount,
                color = Color(0xFFFF9800),
                icon = Icons.Default.Info
            )
        }
        item {
            SummaryCard(
                title = "Pending",
                count = notMarkedCount,
                color = Color(0xFF9E9E9E),
                icon = Icons.Default.AccessTime
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickFilters(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                leadingIcon = {
                    if (selectedFilter == filter) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            )
        }
    }
}

@Composable
private fun AdvancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search employees by name, email, or job title...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Default.Search, contentDescription = "Filters")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = true
    )
}

@Composable
private fun EnhancedEmployeeList(
    employees: List<com.malikhain.employee_attendance_manager.data.entities.Employee>,
    employeesWithAttendance: List<EmployeeWithAttendance>,
    selectedFilter: String,
    onEmployeeClick: (com.malikhain.employee_attendance_manager.data.entities.Employee) -> Unit,
    onMarkAttendance: (Int, String) -> Unit
) {
    val filteredEmployees = when (selectedFilter) {
        "Present" -> employeesWithAttendance.filter { it.attendanceStatus == "Present" }.map { it.employee }
        "Absent" -> employeesWithAttendance.filter { it.attendanceStatus == "Absent" }.map { it.employee }
        "Leave" -> employeesWithAttendance.filter { it.attendanceStatus == "Leave" }.map { it.employee }
        "Not Marked" -> employeesWithAttendance.filter { it.attendanceStatus == null }.map { it.employee }
        else -> employees
    }
    
    if (filteredEmployees.isEmpty()) {
        EmptyStateMessage(selectedFilter != "All")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredEmployees) { employee ->
                val attendanceStatus = employeesWithAttendance.find { it.employee.id == employee.id }?.attendanceStatus
                EnhancedEmployeeCard(
                    employee = employee,
                    attendanceStatus = attendanceStatus,
                    onClick = { onEmployeeClick(employee) },
                    onMarkAttendance = { status -> onMarkAttendance(employee.id, status) }
                )
            }
        }
    }
}

@Composable
private fun EnhancedEmployeeCard(
    employee: com.malikhain.employee_attendance_manager.data.entities.Employee,
    attendanceStatus: String?,
    onClick: () -> Unit,
    onMarkAttendance: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Employee Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Employee Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = employee.jobTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (employee.email != null) {
                    Text(
                        text = employee.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Attendance Status Indicator
                attendanceStatus?.let { status ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (status) {
                                        "Present" -> Color(0xFF4CAF50)
                                        "Absent" -> Color(0xFFF44336)
                                        "Leave" -> Color(0xFFFF9800)
                                        else -> Color(0xFF9E9E9E)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Attendance Buttons
            Column(horizontalAlignment = Alignment.End) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AttendanceButton(
                        text = "P",
                        selected = attendanceStatus == "Present",
                        color = Color(0xFF4CAF50),
                        onClick = { onMarkAttendance("Present") }
                    )
                    AttendanceButton(
                        text = "A",
                        selected = attendanceStatus == "Absent",
                        color = Color(0xFFF44336),
                        onClick = { onMarkAttendance("Absent") }
                    )
                    AttendanceButton(
                        text = "L",
                        selected = attendanceStatus == "Leave",
                        color = Color(0xFFFF9800),
                        onClick = { onMarkAttendance("Leave") }
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceButton(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) color.copy(alpha = 0.1f) else Color.Transparent,
            contentColor = if (selected) color else MaterialTheme.colorScheme.onSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) color else MaterialTheme.colorScheme.outline
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickActionsMenu(
    onDismiss: () -> Unit,
    onBulkMark: () -> Unit,
    onExport: () -> Unit,
    onRefresh: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Actions") },
        text = {
            Column {
                QuickActionItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Bulk Mark Attendance",
                    subtitle = "Mark attendance for all employees",
                    onClick = {
                        onBulkMark()
                        onDismiss()
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.FileDownload,
                    title = "Export Data",
                    subtitle = "Export employee data to CSV",
                    onClick = {
                        onExport()
                        onDismiss()
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.Refresh,
                    title = "Refresh",
                    subtitle = "Refresh employee data",
                    onClick = {
                        onRefresh()
                        onDismiss()
                    }
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
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BulkAttendanceDialog(
    employees: List<com.malikhain.employee_attendance_manager.data.entities.Employee>,
    selectedStatus: String,
    onStatusChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bulk Mark Attendance") },
        text = { 
            Column {
                Text("Mark all employees as:")
                Spacer(modifier = Modifier.height(16.dp))
                
                val statuses = listOf("Present", "Absent", "Leave")
                statuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { onStatusChange(status) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "This will mark attendance for all ${employees.size} employees as $selectedStatus",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Mark All")
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
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search Employees") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun EmptyStateMessage(isSearching: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSearching) {
                Text(
                    text = "No employees found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Try adjusting your search terms",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "No employees yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Add your first employee to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmployeeRow(
    employeeWithAttendance: EmployeeWithAttendance,
    onClick: () -> Unit,
    onMarkAttendance: (String) -> Unit
) {
    val employee = employeeWithAttendance.employee
    val currentStatus = employeeWithAttendance.attendanceStatus
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.name, style = MaterialTheme.typography.titleMedium)
                Text(employee.jobTitle, style = MaterialTheme.typography.bodyMedium)
                if (employee.email != null) {
                    Text(employee.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AttendanceStatusButton(
                    text = "P",
                    selected = currentStatus == "Present",
                    onClick = { onMarkAttendance("Present") }
                )
                AttendanceStatusButton(
                    text = "A",
                    selected = currentStatus == "Absent",
                    onClick = { onMarkAttendance("Absent") }
                )
                AttendanceStatusButton(
                    text = "L",
                    selected = currentStatus == "Leave",
                    onClick = { onMarkAttendance("Leave") }
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatusButton(text: String, selected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(0.dp),
        colors = if (selected) ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) else ButtonDefaults.outlinedButtonColors()
    ) {
        Text(text)
    }
}

// TODO: Add sorting and filtering options for employees (by name, department, status)
// TODO: Implement bulk attendance marking for multiple employees
// TODO: Display employee avatars/profile pictures
// TODO: Add export/print functionality for employee list
// TODO: Implement pagination or lazy loading for large lists
// TODO: Display department/role in the employee list if available
// TODO: Show feedback (snackbar/toast) on successful attendance marking

// TODO: Implement quick filters (department, status, date range)
// TODO: Add bulk attendance operations with confirmation dialogs
// TODO: Display employee avatars/profile pictures with fallback initials
// TODO: Add export functionality (CSV, Excel, PDF) for employee data
// TODO: Implement pull-to-refresh for real-time data updates
// TODO: Show loading states and skeleton screens during data fetch
// TODO: Implement sorting options (name, department, attendance status, hire date)
// TODO: Add employee status indicators (active, inactive, on leave)
// TODO: Show attendance trends and patterns
// TODO: Add quick actions menu for common operations
// TODO: Implement search suggestions and autocomplete
// TODO: Add employee categories/departments management
// TODO: Show recent activity feed and notifications
// TODO: Add attendance analytics and insights
// TODO: Implement employee performance indicators
// TODO: Add holiday calendar integration
// TODO: Show upcoming birthdays and work anniversaries
// TODO: Add employee search with advanced filters
// TODO: Implement employee status badges and indicators
// TODO: Add attendance history quick view
// TODO: Show department-wise attendance summaries
// TODO: Add employee contact quick actions (call, email, message)
// TODO: Implement attendance reminders and notifications
// TODO: Add employee performance metrics display
// TODO: Show attendance compliance rates
// TODO: Add employee onboarding status indicators
// TODO: Implement employee satisfaction/feedback system
// TODO: Add attendance anomaly detection and alerts
// TODO: Show employee workload and overtime indicators
// TODO: Add employee skill tags and certifications display
// TODO: Implement employee shift management
// TODO: Add employee leave balance display
// TODO: Show employee attendance streak counters
// TODO: Add employee performance trends over time
// TODO: Implement employee goal tracking and achievements
// TODO: Add employee communication preferences
// TODO: Show employee emergency contact information
// TODO: Add employee document management (contracts, certificates)
// TODO: Implement employee training and development tracking
// TODO: Add employee salary and compensation information
// TODO: Show employee work schedule and availability
// TODO: Add employee performance reviews and feedback
// TODO: Implement employee recognition and rewards system
