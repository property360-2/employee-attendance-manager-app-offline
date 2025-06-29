package com.malikhain.employee_attendance_manager.screens.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.malikhain.employee_attendance_manager.viewmodel.EmployeeWithAttendance

@Composable
fun EnhancedEmployeeCard(
    employeeWithAttendance: EmployeeWithAttendance,
    onClick: () -> Unit,
    onMarkAttendance: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val employee = employeeWithAttendance.employee
    val currentStatus = employeeWithAttendance.attendanceStatus
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .scale(scale)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Section
            EmployeeAvatar(
                name = employee.name,
                email = employee.email,
                modifier = Modifier.size(56.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Employee Info Section
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = employee.jobTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (employee.email != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = employee.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                if (employee.phone != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = employee.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Attendance Status Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Current Status Indicator
                StatusIndicator(
                    status = currentStatus,
                    modifier = Modifier.size(8.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Attendance Buttons
                EnhancedAttendanceButtons(
                    currentStatus = currentStatus,
                    onMarkAttendance = onMarkAttendance
                )
            }
        }
    }
}

@Composable
private fun EmployeeAvatar(
    name: String,
    email: String?,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}

@Composable
private fun StatusIndicator(
    status: String?,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = when (status) {
            "Present" -> MaterialTheme.colorScheme.primary
            "Absent" -> MaterialTheme.colorScheme.error
            "Leave" -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(durationMillis = 300),
        label = "statusColor"
    )
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun EnhancedAttendanceButtons(
    currentStatus: String?,
    onMarkAttendance: (String) -> Unit
) {
    val statuses = listOf("Present", "Absent", "Leave")
    
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        statuses.forEach { status ->
            val isSelected = currentStatus == status
            val backgroundColor = when (status) {
                "Present" -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                "Absent" -> if (isSelected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface
                "Leave" -> if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface
            }
            
            val textColor = when (status) {
                "Present" -> if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                "Absent" -> if (isSelected) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.error
                "Leave" -> if (isSelected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.onSurface
            }
            
            val icon = when (status) {
                "Present" -> Icons.Default.Check
                "Absent" -> Icons.Default.Close
                "Leave" -> Icons.Default.Info
                else -> Icons.Default.Info
            }
            
            OutlinedButton(
                onClick = { onMarkAttendance(status) },
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = backgroundColor
                ),
                border = if (isSelected) null else null
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = status,
                    modifier = Modifier.size(16.dp),
                    tint = textColor
                )
            }
        }
    }
} 