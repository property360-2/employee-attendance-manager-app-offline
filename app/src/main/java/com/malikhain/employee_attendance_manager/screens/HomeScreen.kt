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
