package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.navigation.Screen
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeWithAttendance
import com.malikhain.employee_attendance_manager.screens.components.DashboardStats
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: EmployeeViewModel = hiltViewModel()
    val employees by viewModel.employees.collectAsState()
    val employeesWithAttendance by viewModel.employeesWithAttendance.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showBulkMarkingDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Present") }
    
    LaunchedEffect(Unit) {
        viewModel.loadEmployees()
    }
    
    LaunchedEffect(searchQuery) {
        viewModel.searchEmployees(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate("analytics") }) {
                        Icon(Icons.Default.Info, contentDescription = "Analytics")
                    }
                    IconButton(onClick = { showBulkMarkingDialog = true }) {
                        Icon(Icons.Default.Check, contentDescription = "Bulk Mark Attendance")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_employee") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search employees...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
            
            // Dashboard Stats
            if (employeesWithAttendance.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Today's Summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total Employees: ${employees.size}")
                        Text("Present: ${employeesWithAttendance.count { it.attendanceStatus == "Present" }}")
                        Text("Absent: ${employeesWithAttendance.count { it.attendanceStatus == "Absent" }}")
                    }
                }
            }
            
            // Employee List
            if (employees.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No employees found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Add your first employee to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate("add_employee") }) {
                            Text("Add Employee")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(employees) { employee ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    employee.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    employee.jobTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = { viewModel.markAttendance(employee.id, "Present") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Present")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.markAttendance(employee.id, "Absent") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Absent")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Bulk Attendance Marking Dialog
        if (showBulkMarkingDialog) {
            AlertDialog(
                onDismissRequest = { showBulkMarkingDialog = false },
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
                                    onClick = { selectedStatus = status }
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
                    TextButton(
                        onClick = {
                            employees.forEach { employee ->
                                viewModel.markAttendance(employee.id, selectedStatus)
                            }
                            showBulkMarkingDialog = false
                        }
                    ) {
                        Text("Mark All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBulkMarkingDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
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
