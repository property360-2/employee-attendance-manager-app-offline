package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel
import com.malikhain.employee_attendance_manager.data.entities.Employee
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployeeScreen(navController: NavController, id: Int) {
    val viewModel: EmployeeViewModel = hiltViewModel()
    val employees by viewModel.employees.collectAsState()
    val employee = employees.find { it.id == id }

    if (employee == null) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Edit Employee") })
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Employee not found.")
            }
        }
        return
    }

    var name by remember { mutableStateOf(employee.name) }
    var jobTitle by remember { mutableStateOf(employee.jobTitle) }
    var email by remember { mutableStateOf(employee.email ?: "") }
    var phone by remember { mutableStateOf(employee.phone ?: "") }
    var address by remember { mutableStateOf(employee.address ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit ${employee.name}") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit Employee", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    if (name.isBlank() || jobTitle.isBlank()) {
                        error = "Name and Job Title are required"
                    } else {
                        error = null
                        viewModel.updateEmployee(
                            employee.copy(
                                name = name,
                                jobTitle = jobTitle,
                                email = email.ifBlank { null },
                                phone = phone.ifBlank { null },
                                address = address.ifBlank { null }
                            )
                        )
                        navController.popBackStack()
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Save")
                }
            }
            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}