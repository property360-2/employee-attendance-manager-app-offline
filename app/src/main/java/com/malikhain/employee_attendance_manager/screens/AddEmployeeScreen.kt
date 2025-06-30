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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowBack
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(navController: NavController) {
    val viewModel: EmployeeViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Employee") },
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
            Text("Add Employee", style = MaterialTheme.typography.titleLarge)
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
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (name.isBlank() || jobTitle.isBlank()) {
                            error = "Name and Job Title are required"
                        } else {
                            isLoading = true
                            error = null
                            try {
                                viewModel.addEmployee(
                                    Employee(
                                        name = name.trim(),
                                        jobTitle = jobTitle.trim(),
                                        email = email.trim().ifBlank { null },
                                        phone = phone.trim().ifBlank { null },
                                        address = address.trim().ifBlank { null }
                                    )
                                )
                                navController.navigateUp()
                            } catch (e: Exception) {
                                error = "Failed to add employee. Please try again."
                                isLoading = false
                            }
                        }
                    }, 
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && name.isNotBlank() && jobTitle.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Save")
                }
            }
        }
    }
}

// TODO: Validate email and phone formats, highlight required fields
// TODO: Allow uploading a profile picture for the employee
// TODO: Add dropdowns for department and role selection
// TODO: Show confirmation/snackbar on successful addition
// TODO: Prompt before discarding unsaved changes
// TODO: Warn if an employee with the same email/phone already exists
// TODO: Visually mark required fields
// TODO: Allow adding custom fields (hire date, notes)

// TODO: Add profile picture upload with camera integration
// TODO: Implement department/role dropdowns with custom options
// TODO: Add hire date picker with calendar integration
// TODO: Implement employee ID auto-generation system
// TODO: Add duplicate email/phone validation with real-time feedback
// TODO: Implement form validation with visual indicators and error messages
// TODO: Add camera integration for profile photo capture
// TODO: Show confirmation dialog and success feedback
// TODO: Implement auto-save draft functionality
// TODO: Add employee templates for quick addition
// TODO: Show field validation in real-time with helpful messages
// TODO: Add bulk import functionality from CSV/Excel
// TODO: Implement employee ID auto-generation with prefix/suffix options
// TODO: Add custom fields support (emergency contacts, skills, certifications)
// TODO: Add multi-step form wizard for better UX
// TODO: Implement employee onboarding workflow
// TODO: Add employee status selection (active, inactive, probation)
// TODO: Show employee photo preview and editing options
// TODO: Add employee category/tag system
// TODO: Implement employee referral tracking
// TODO: Add employee contract type selection
// TODO: Show employee work location and remote work options
// TODO: Add employee supervisor/manager assignment
// TODO: Implement employee salary information (optional)
// TODO: Add employee work schedule preferences
// TODO: Show employee emergency contact information
// TODO: Add employee skill assessment and rating
// TODO: Implement employee training requirements
// TODO: Add employee performance goals and objectives
// TODO: Show employee communication preferences
// TODO: Add employee health and safety information
// TODO: Implement employee equipment and resource allocation
// TODO: Add employee parking and transportation preferences
// TODO: Show employee dietary restrictions and preferences
// TODO: Add employee language and communication skills
// TODO: Implement employee cultural and diversity information
// TODO: Add employee performance history and references
// TODO: Show employee education and certification details
// TODO: Add employee work experience and background
// TODO: Implement employee social media and professional profiles
// TODO: Add employee personal development goals
// TODO: Show employee team and project assignments
// TODO: Add employee work environment preferences
// TODO: Implement employee feedback and survey responses
// TODO: Add employee recognition and achievement history
// TODO: Show employee attendance and punctuality history
// TODO: Add employee health and wellness information
// TODO: Implement employee career development plans
// TODO: Add employee retirement and benefits information
// TODO: Show employee family and dependent information
// TODO: Add employee legal and compliance information
// TODO: Implement employee security clearance and access levels