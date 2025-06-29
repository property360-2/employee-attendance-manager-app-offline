package com.malikhain.employee_attendance_manager.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import com.malikhain.employee_attendance_manager.data.entities.Employee
import com.malikhain.employee_attendance_manager.navigation.BottomNavigationBar
import com.malikhain.employee_attendance_manager.viewmodel.AttendanceViewViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceViewScreen(
    navController: NavController,
    viewModel: AttendanceViewViewModel = hiltViewModel()
) {
    val employees by viewModel.employees.collectAsState()
    val attendance by viewModel.filteredAttendance.collectAsState()
    val filterType by viewModel.filterType.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Attendance Records") }) },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            FilterControls(
                selectedFilter = filterType,
                onFilterSelected = { viewModel.setFilterType(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AttendanceTable(employees, attendance)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterControls(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val filters = listOf("Day", "Week", "Month")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        SingleChoiceSegmentedButtonRow {
            filters.forEachIndexed { index, filter ->
                SegmentedButton(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelected(filter) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = filters.size)
                ) {
                    Text(filter)
                }
            }
        }
    }
}

@Composable
private fun AttendanceTable(employees: List<Employee>, attendance: List<Attendance>) {
    val viewModel: AttendanceViewViewModel = hiltViewModel()
    if (employees.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No employees to display.")
        }
        return
    }

    val attendanceMap = attendance.groupBy { it.employeeId }
    val dates = attendance.map {
        Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
    }.distinct().sorted()

    val horizontalScrollState = rememberScrollState()

    Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
        // Header Row
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Employee",
                modifier = Modifier.width(150.dp).padding(8.dp),
                style = MaterialTheme.typography.titleSmall
            )
            dates.forEach { date ->
                Text(
                    date.format(DateTimeFormatter.ofPattern("dd/MM")),
                    modifier = Modifier.width(60.dp).padding(8.dp),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        // Employee Rows
        LazyColumn {
            items(employees, key = { it.id }) { employee ->
                Row(
                    modifier = Modifier.border(width = 0.5.dp, color = MaterialTheme.colorScheme.outline),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        employee.name,
                        modifier = Modifier.width(150.dp).padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    dates.forEach { date ->
                        val record = attendanceMap[employee.id]?.find {
                            Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() == date
                        }
                        AttendanceCell(
                            employeeId = employee.id,
                            date = date,
                            status = record?.status,
                            onStatusChange = { status ->
                                // Convert date to millis at start of day
                                val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                viewModel.editAttendance(employee.id, status, millis)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceCell(
    employeeId: Int,
    date: LocalDate,
    status: String?,
    onStatusChange: (String) -> Unit
) {
    val statuses = listOf("Present", "Absent", "Leave")
    Row(
        modifier = Modifier
            .width(60.dp)
            .height(52.dp)
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        statuses.forEach { s ->
            IconButton(
                onClick = { if (status != s) onStatusChange(s) },
                modifier = Modifier.size(28.dp)
            ) {
                val (icon, color) = when (s) {
                    "Present" -> Icons.Default.Check to MaterialTheme.colorScheme.primaryContainer
                    "Absent" -> Icons.Default.Close to MaterialTheme.colorScheme.errorContainer
                    "Leave" -> Icons.Default.Info to MaterialTheme.colorScheme.tertiaryContainer
                    else -> null to MaterialTheme.colorScheme.surface
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            if (status == s) color else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = s,
                            tint = contentColorFor(color),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// TODO: Allow exporting attendance data to CSV/PDF or printing
// TODO: Show summary statistics (totals, averages) for attendance
// TODO: Add search by employee name/ID and filter by department
// TODO: Allow picking a custom date range for attendance view
// TODO: Enable bulk editing of attendance for multiple employees/days
// TODO: Highlight frequent absences or attendance anomalies
// TODO: Allow adding notes to specific attendance records 