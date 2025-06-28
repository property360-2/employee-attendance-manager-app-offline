package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.navigation.Screen
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeWithAttendance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: EmployeeViewModel) {
    val employeesWithAttendance by viewModel.employeesWithAttendance.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredEmployees = employeesWithAttendance.filter {
        it.employee.name.contains(searchQuery, ignoreCase = true) ||
        it.employee.jobTitle.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Employee Dashboard") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddEmployee.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(filteredEmployees, key = { it.employee.id }) { employeeWithAttendance ->
                    EmployeeRow(
                        employeeWithAttendance = employeeWithAttendance,
                        onMarkAttendance = { status ->
                            viewModel.markAttendance(employeeWithAttendance.employee.id, status)
                        },
                        onClick = { navController.navigate(Screen.EmployeeDetails.createRoute(employeeWithAttendance.employee.id)) }
                    )
                }
            }
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
            .padding(vertical = 4.dp)
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
// TODO: Show dashboard summary (total employees, present/absent today)
// TODO: Add empty state illustration/message when no employees are present
// TODO: Show feedback (snackbar/toast) on successful attendance marking
// TODO: Add export/print functionality for employee list
// TODO: Implement pagination or lazy loading for large lists
// TODO: Display department/role in the employee list if available

// TODO: Add dashboard statistics cards (total employees, present/absent today, attendance rate)
// TODO: Implement quick filters (department, status, date range)
// TODO: Add bulk attendance operations with confirmation dialogs
// TODO: Display employee avatars/profile pictures with fallback initials
// TODO: Add export functionality (CSV, Excel, PDF) for employee data
// TODO: Implement pull-to-refresh for real-time data updates
// TODO: Add empty state illustrations and helpful messages
// TODO: Show loading states and skeleton screens during data fetch
// TODO: Add dashboard widgets/cards for key metrics
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
