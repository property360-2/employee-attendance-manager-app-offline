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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
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
            TopAppBar(
                title = { Text("Edit ${employee.name}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                onValueChange = { 
                    name = it
                    if (error != null) error = null
                },
                label = { Text("Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error != null && name.isBlank()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { 
                    jobTitle = it
                    if (error != null) error = null
                },
                label = { Text("Job Title *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error != null && jobTitle.isBlank()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    if (error != null) error = null
                },
                label = { Text("Email (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = phone,
                onValueChange = { 
                    phone = it
                    if (error != null) error = null
                },
                label = { Text("Phone (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = address,
                onValueChange = { 
                    address = it
                    if (error != null) error = null
                },
                label = { Text("Address (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            error?.let {
                Text(
                    it, 
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigateUp() }, 
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (name.isBlank() || jobTitle.isBlank()) {
                            error = "Name and Job Title are required"
                        } else {
                            error = null
                            try {
                                viewModel.updateEmployee(
                                    employee.copy(
                                        name = name.trim(),
                                        jobTitle = jobTitle.trim(),
                                        email = email.trim().ifBlank { null },
                                        phone = phone.trim().ifBlank { null },
                                        address = address.trim().ifBlank { null }
                                    )
                                )
                                navController.navigateUp()
                            } catch (e: Exception) {
                                error = "Failed to update employee. Please try again."
                            }
                        }
                    }, 
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && jobTitle.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

// TODO: Add a button to delete the employee, with confirmation dialog
// TODO: Allow changing the employee's profile picture
// TODO: Show a history of changes (audit trail) for the employee's record
// TODO: Show snackbar/toast on successful update or deletion
// TODO: Add undo/redo for recent changes before saving
// TODO: Add confirmation dialog before saving or deleting
// TODO: Validate fields (email, phone, required fields)

// TODO: Implement delete employee functionality with confirmation dialog
// TODO: Add profile picture management with upload/change options
// TODO: Show change history and audit trail for employee records
// TODO: Display success feedback with snackbar/toast messages
// TODO: Implement undo/redo functionality for recent changes
// TODO: Add confirmation dialogs for all destructive actions
// TODO: Implement comprehensive field validation (email, phone, required fields)
// TODO: Add employee status management (active, inactive, terminated)
// TODO: Implement bulk edit capabilities for multiple employees
// TODO: Add employee deactivation with reason tracking
// TODO: Show edit history with timestamp and admin details
// TODO: Implement field-level permissions and access control
// TODO: Add employee transfer functionality between departments
// TODO: Show employee performance history and trends
// TODO: Implement employee promotion and demotion tracking
// TODO: Add employee contract renewal and termination management
// TODO: Show employee training and certification updates
// TODO: Implement employee salary and compensation changes
// TODO: Add employee work schedule modifications
// TODO: Show employee role and responsibility changes
// TODO: Implement employee supervisor/manager reassignment
// TODO: Add employee location and workspace changes
// TODO: Show employee equipment and resource allocation updates
// TODO: Implement employee access level and security changes
// TODO: Add employee emergency contact updates
// TODO: Show employee health and safety information changes
// TODO: Implement employee benefits and insurance updates
// TODO: Add employee performance review and feedback updates
// TODO: Show employee goal and objective modifications
// TODO: Implement employee recognition and reward updates
// TODO: Add employee disciplinary action tracking
// TODO: Show employee leave balance and policy changes
// TODO: Implement employee work environment preference updates
// TODO: Add employee communication preference changes
// TODO: Show employee skill and certification updates
// TODO: Implement employee project and team assignment changes
// TODO: Add employee retirement and exit planning
// TODO: Show employee family and dependent information updates
// TODO: Implement employee legal and compliance updates
// TODO: Add employee cultural and diversity information changes
// TODO: Show employee social media and professional profile updates
// TODO: Implement employee personal development goal changes
// TODO: Add employee health and wellness information updates
// TODO: Show employee career development plan modifications
// TODO: Implement employee feedback and survey response updates
// TODO: Add employee recognition and achievement history updates
// TODO: Show employee attendance and punctuality history changes
// TODO: Implement employee equipment and resource deallocation
// TODO: Add employee parking and transportation preference updates
// TODO: Show employee dietary restriction and preference changes
// TODO: Implement employee language and communication skill updates
// TODO: Add employee performance history and reference updates
// TODO: Show employee education and certification detail changes
// TODO: Implement employee work experience and background updates
// TODO: Add employee social media and professional profile changes
// TODO: Show employee personal development goal modifications
// TODO: Implement employee team and project assignment updates
// TODO: Add employee work environment preference changes
// TODO: Show employee feedback and survey response modifications
// TODO: Implement employee recognition and achievement history updates
// TODO: Add employee attendance and punctuality history changes
// TODO: Show employee health and wellness information modifications
// TODO: Implement employee career development plan updates
// TODO: Add employee retirement and benefits information changes
// TODO: Show employee family and dependent information modifications
// TODO: Implement employee legal and compliance information updates
// TODO: Add employee security clearance and access level changes