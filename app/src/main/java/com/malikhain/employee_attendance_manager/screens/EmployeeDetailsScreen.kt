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