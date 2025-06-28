package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailsScreen(navController: NavController, id: Int) {
    val viewModel: EmployeeViewModel = hiltViewModel()
    val employees by viewModel.employees.collectAsState()
    val employee = employees.find { it.id == id }

    if (employee == null) {
        // Show error UI
        return
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Info", "Attendance")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(employee.name) },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTab) {
                0 -> InfoTab(employee = employee, navController = navController)
                1 -> AttendanceTab(employeeId = employee.id)
            }
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
// TODO: Show employee legal and compliance information management
// TODO: Implement employee security clearance and access level management