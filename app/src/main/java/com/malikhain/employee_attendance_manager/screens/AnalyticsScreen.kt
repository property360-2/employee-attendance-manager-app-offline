package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import com.malikhain.employee_attendance_manager.data.entities.Employee
import com.malikhain.employee_attendance_manager.viewmodel.AttendanceViewModel
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController) {
    val attendanceViewModel: AttendanceViewModel = hiltViewModel()
    val employeeViewModel: EmployeeViewModel = hiltViewModel()
    val allAttendance by attendanceViewModel.allAttendance.collectAsState()
    val employees by employeeViewModel.employees.collectAsState()
    
    LaunchedEffect(Unit) {
        attendanceViewModel.loadAllAttendance()
        employeeViewModel.loadEmployees()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Attendance Analytics",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                AttendanceSummaryCard(allAttendance, employees)
            }
            
            item {
                WeeklyTrendCard(allAttendance)
            }
            
            item {
                StatusDistributionCard(allAttendance)
            }
        }
    }
}

@Composable
private fun AttendanceSummaryCard(attendance: List<Attendance>, employees: List<Employee>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Summary",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total Employees",
                    value = employees.size.toString(),
                    icon = Icons.Default.Person
                )
                
                StatItem(
                    label = "Total Records",
                    value = attendance.size.toString(),
                    icon = Icons.Default.Check
                )
                
                StatItem(
                    label = "Present",
                    value = attendance.count { it.status == "Present" }.toString(),
                    icon = Icons.Default.Person
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Absent",
                    value = attendance.count { it.status == "Absent" }.toString(),
                    icon = Icons.Default.Person
                )
                
                StatItem(
                    label = "Leave",
                    value = attendance.count { it.status == "Leave" }.toString(),
                    icon = Icons.Default.Info
                )
                
                StatItem(
                    label = "Attendance Rate",
                    value = if (attendance.isNotEmpty()) {
                        val presentCount = attendance.count { it.status == "Present" }
                        "${(presentCount * 100 / attendance.size)}%"
                    } else "0%",
                    icon = Icons.Default.Check
                )
            }
        }
    }
}

@Composable
private fun WeeklyTrendCard(attendance: List<Attendance>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Weekly Trend",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple weekly chart
            val calendar = Calendar.getInstance()
            val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            
            weekDays.forEachIndexed { index, day ->
                val dayAttendance = attendance.filter { 
                    calendar.time = Date(it.date)
                    calendar.get(Calendar.DAY_OF_WEEK) == (Calendar.MONDAY + index)
                }
                
                val presentCount = dayAttendance.count { it.status == "Present" }
                val totalCount = dayAttendance.size
                val percentage = if (totalCount > 0) (presentCount * 100 / totalCount) else 0
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        day,
                        modifier = Modifier.width(40.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    LinearProgressIndicator(
                        progress = percentage / 100f,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        "$percentage%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusDistributionCard(attendance: List<Attendance>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Status Distribution",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val presentCount = attendance.count { it.status == "Present" }
            val absentCount = attendance.count { it.status == "Absent" }
            val leaveCount = attendance.count { it.status == "Leave" }
            val total = attendance.size
            
            if (total > 0) {
                StatusBar("Present", presentCount, total, MaterialTheme.colorScheme.primary)
                StatusBar("Absent", absentCount, total, MaterialTheme.colorScheme.error)
                StatusBar("Leave", leaveCount, total, MaterialTheme.colorScheme.tertiary)
            } else {
                Text(
                    "No attendance data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusBar(label: String, count: Int, total: Int, color: Color) {
    val percentage = if (total > 0) (count * 100 / total) else 0
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.width(60.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier.weight(1f),
            color = color
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            "$count ($percentage%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 