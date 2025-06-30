package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import com.malikhain.employee_attendance_manager.data.entities.Employee
import com.malikhain.employee_attendance_manager.navigation.Screen
import com.malikhain.employee_attendance_manager.viewmodel.AttendanceViewModel
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailsScreen(
    navController: NavController,
    employeeId: Int
) {
    val viewModel: EmployeeViewModel = hiltViewModel()
    val employee by viewModel.getEmployee(employeeId).collectAsState()
    val attendance by viewModel.getEmployeeAttendance(employeeId).collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    
    LaunchedEffect(employeeId) {
        viewModel.refreshEmployeeData(employeeId)
        viewModel.loadEmployeeAttendance(employeeId)
        
        // Show error after 3 seconds if employee is still null
        kotlinx.coroutines.delay(3000)
        if (employee == null) {
            showError = true
        }
    }

    // Show loading state while data is being fetched
    if (employee == null && !showError) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Employee Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading employee details...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ID: $employeeId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }
    
    // Show error state if employee is not found after timeout
    if (employee == null && showError) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Employee Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Employee not found",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Employee not found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "The employee with ID $employeeId doesn't exist or has been deleted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.navigateUp() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(employee!!.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("edit/${employeeId}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Employee Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Employee Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    InfoRow("Name", employee!!.name)
                    InfoRow("Job Title", employee!!.jobTitle)
                    employee!!.email?.let { InfoRow("Email", it) }
                    employee!!.phone?.let { InfoRow("Phone", it) }
                    employee!!.address?.let { InfoRow("Address", it) }
                    InfoRow("Created Date", SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(employee!!.createdAt)))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Attendance Section
            Text(
                text = "Attendance History",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (attendance.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No attendance records found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attendance) { attendanceRecord ->
                        AttendanceCard(attendanceRecord)
                    }
                }
            }
        }
        
        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Employee") },
                text = { 
                    Text(
                        "Are you sure you want to delete ${employee!!.name}? This action cannot be undone and will also delete all attendance records.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteEmployee(employee!!)
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun InfoTab(employee: Employee, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("Job Title: ${employee.jobTitle}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        employee.email?.let {
            Text("Email: $it", style = MaterialTheme.typography.bodyLarge)
        }
        employee.phone?.let {
            Text("Phone: $it", style = MaterialTheme.typography.bodyLarge)
        }
        employee.address?.let {
            Text("Address: $it", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate(Screen.EditEmployee.createRoute(employee.id)) }) {
            Text("Edit Employee Details")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceTab(employeeId: Int) {
    val viewModel: AttendanceViewModel = hiltViewModel()
    val records by viewModel.attendanceRecords(employeeId).collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Present") }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val alreadyMarked = records.any { isToday(it.date) }

    var statusFilter by remember { mutableStateOf<String?>(null) }

    val filteredRecords = records.filter {
        statusFilter == null || it.status == statusFilter
    }

    Scaffold(
        floatingActionButton = {
            if (!alreadyMarked) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Mark Attendance")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilterControls(
                statusFilter = statusFilter,
                onStatusChange = { statusFilter = it }
            )
            if (filteredRecords.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No attendance records match the filter.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredRecords, key = { it.attendanceId }) { record ->
                        AttendanceListItem(record)
                    }
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Mark Attendance for $today") },
                text = {
                    Column {
                        Text("Select status:")
                        Spacer(modifier = Modifier.height(8.dp))
                        AttendanceStatusSelector(selectedStatus) { selectedStatus = it }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.markAttendance(
                            Attendance(
                                employeeId = employeeId,
                                date = System.currentTimeMillis(),
                                status = selectedStatus
                            )
                        )
                        showDialog = false
                    }) { Text("Mark") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterControls(statusFilter: String?, onStatusChange: (String?) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Filter by:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        val statuses = listOf("Present", "Absent", "Leave")
        statuses.forEach { status ->
            FilterChip(
                selected = statusFilter == status,
                onClick = {
                    onStatusChange(if (statusFilter == status) null else status)
                },
                label = { Text(status) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun AttendanceListItem(record: Attendance) {
    val viewModel: AttendanceViewModel = hiltViewModel()
    val statuses = listOf("Present", "Absent", "Leave")
    val currentStatus = record.status
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(record.date))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(date, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                statuses.forEach { status ->
                    OutlinedButton(
                        onClick = {
                            if (currentStatus != status) {
                                viewModel.editAttendance(record.employeeId, status, record.date)
                            }
                        },
                        colors = if (currentStatus == status) ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(status.first().toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceStatusSelector(selected: String, onSelect: (String) -> Unit) {
    val statuses = listOf("Present", "Absent", "Leave")
    Row {
        statuses.forEach { status ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selected == status,
                    onClick = { onSelect(status) }
                )
                Text(status)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

private fun isToday(timestamp: Long): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = sdf.format(Date())
    val date = sdf.format(Date(timestamp))
    return today == date
}

// TODO: Display the employee's photo prominently
// TODO: Add buttons to call, email, or message the employee
// TODO: Show attendance summary (e.g., % present this month, last absent date)
// TODO: Allow adding notes/comments about the employee
// TODO: Add more tabs if needed (e.g., Documents, Performance)
// TODO: Allow exporting/printing employee details and attendance
// TODO: Allow uploading/viewing documents related to the employee
// TODO: Add more info fields (hire date, department, supervisor, etc.)

// TODO: Add employee photo gallery and profile picture management
// TODO: Implement performance metrics and analytics display
// TODO: Add leave balance tracking and management
// TODO: Show salary information and compensation details
// TODO: Add emergency contacts management
// TODO: Implement work schedule display and management
// TODO: Add performance reviews and feedback system
// TODO: Show employee statistics and attendance analytics
// TODO: Implement attendance patterns and trend analysis
// TODO: Add performance tracking and goal management
// TODO: Show leave management and request system
// TODO: Implement document attachments and file management
// TODO: Add employee training and development tracking
// TODO: Show employee skills and certifications
// TODO: Implement employee projects and assignments
// TODO: Add employee communication preferences
// TODO: Show employee work environment preferences
// TODO: Implement employee feedback and survey system
// TODO: Add employee recognition and achievements
// TODO: Show employee attendance history and patterns
// TODO: Implement employee performance trends over time
// TODO: Add employee goal tracking and achievements
// TODO: Show employee communication history
// TODO: Implement employee emergency contact management
// TODO: Add employee document management (contracts, certificates)
// TODO: Show employee training and development progress
// TODO: Implement employee salary and compensation history
// TODO: Add employee work schedule and availability management
// TODO: Show employee performance reviews and feedback history
// TODO: Implement employee recognition and rewards system
// TODO: Add employee disciplinary actions and warnings
// TODO: Show employee leave history and patterns
// TODO: Implement employee work environment preferences
// TODO: Add employee communication preference management
// TODO: Show employee skill assessment and development
// TODO: Implement employee project and team assignment history
// TODO: Add employee retirement and exit planning
// TODO: Show employee family and dependent information
// TODO: Implement employee legal and compliance information
// TODO: Add employee security clearance and access levels
// TODO: Show employee cultural and diversity information
// TODO: Implement employee social media and professional profiles
// TODO: Add employee personal development goals
// TODO: Show employee health and wellness information
// TODO: Implement employee career development plans
// TODO: Add employee benefits and insurance information
// TODO: Show employee equipment and resource allocation
// TODO: Implement employee parking and transportation preferences
// TODO: Add employee dietary restrictions and preferences
// TODO: Show employee language and communication skills
// TODO: Implement employee performance history and references
// TODO: Add employee education and certification details
// TODO: Show employee work experience and background
// TODO: Implement employee social media and professional profile management
// TODO: Add employee personal development goal tracking
// TODO: Show employee team and project assignments
// TODO: Implement employee work environment preference management
// TODO: Add employee feedback and survey response tracking
// TODO: Show employee recognition and achievement history
// TODO: Implement employee attendance and punctuality history
// TODO: Add employee health and wellness information management
// TODO: Show employee career development plan progress
// TODO: Implement employee retirement and benefits information management
// TODO: Add employee family and dependent information management

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AttendanceCard(attendance: Attendance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(attendance.date)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(attendance.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            val statusColor = when (attendance.status) {
                "Present" -> MaterialTheme.colorScheme.primary
                "Absent" -> MaterialTheme.colorScheme.error
                "Leave" -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            
            Text(
                text = attendance.status,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
        }
    }
}