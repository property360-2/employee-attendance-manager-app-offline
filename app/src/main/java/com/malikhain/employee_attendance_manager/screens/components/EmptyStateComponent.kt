package com.malikhain.employee_attendance_manager.screens.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.malikhain.employee_attendance_manager.ui.theme.*

enum class EmptyStateType {
    NO_EMPLOYEES,
    NO_SEARCH_RESULTS,
    NO_ATTENDANCE_DATA,
    NO_BACKUP_DATA,
    NO_NOTIFICATIONS,
    LOADING_ERROR
}

@Composable
fun ModernEmptyState(
    type: EmptyStateType,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = when (type) {
                                EmptyStateType.NO_EMPLOYEES -> listOf(ModernPrimary, ModernPrimaryLight)
                                EmptyStateType.NO_SEARCH_RESULTS -> listOf(ModernInfo, ModernInfoLight)
                                EmptyStateType.NO_ATTENDANCE_DATA -> listOf(ModernSecondary, ModernSecondaryLight)
                                EmptyStateType.NO_BACKUP_DATA -> listOf(ModernTertiary, ModernTertiaryLight)
                                EmptyStateType.NO_NOTIFICATIONS -> listOf(ModernNeutral500, ModernNeutral400)
                                EmptyStateType.LOADING_ERROR -> listOf(ModernError, ModernErrorLight)
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (type) {
                        EmptyStateType.NO_EMPLOYEES -> Icons.Default.Person
                        EmptyStateType.NO_SEARCH_RESULTS -> Icons.Default.Info
                        EmptyStateType.NO_ATTENDANCE_DATA -> Icons.Default.Info
                        EmptyStateType.NO_BACKUP_DATA -> Icons.Default.Info
                        EmptyStateType.NO_NOTIFICATIONS -> Icons.Default.Info
                        EmptyStateType.LOADING_ERROR -> Icons.Default.Close
                    },
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = when (type) {
                    EmptyStateType.NO_EMPLOYEES -> "No Employees Yet"
                    EmptyStateType.NO_SEARCH_RESULTS -> "No Results Found"
                    EmptyStateType.NO_ATTENDANCE_DATA -> "No Attendance Data"
                    EmptyStateType.NO_BACKUP_DATA -> "No Backup Data"
                    EmptyStateType.NO_NOTIFICATIONS -> "No Notifications"
                    EmptyStateType.LOADING_ERROR -> "Something Went Wrong"
                },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description
            Text(
                text = when (type) {
                    EmptyStateType.NO_EMPLOYEES -> "Get started by adding your first employee to the system. This will help you track attendance and manage your team effectively."
                    EmptyStateType.NO_SEARCH_RESULTS -> "Try adjusting your search terms or filters. You can search by name, job title, or email address."
                    EmptyStateType.NO_ATTENDANCE_DATA -> "No attendance records found for the selected period. Start marking attendance to see data here."
                    EmptyStateType.NO_BACKUP_DATA -> "No backup data available. Create your first backup to secure your employee and attendance data."
                    EmptyStateType.NO_NOTIFICATIONS -> "You're all caught up! No new notifications at the moment."
                    EmptyStateType.LOADING_ERROR -> "We encountered an issue loading your data. Please check your connection and try again."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
            )
            
            // Action Button
            onActionClick?.let { action ->
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = action,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (type) {
                            EmptyStateType.NO_EMPLOYEES -> ModernPrimary
                            EmptyStateType.NO_SEARCH_RESULTS -> ModernInfo
                            EmptyStateType.NO_ATTENDANCE_DATA -> ModernSecondary
                            EmptyStateType.NO_BACKUP_DATA -> ModernTertiary
                            EmptyStateType.NO_NOTIFICATIONS -> ModernNeutral500
                            EmptyStateType.LOADING_ERROR -> ModernError
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = when (type) {
                            EmptyStateType.NO_EMPLOYEES -> Icons.Default.Add
                            EmptyStateType.NO_SEARCH_RESULTS -> Icons.Default.Check
                            EmptyStateType.NO_ATTENDANCE_DATA -> Icons.Default.Check
                            EmptyStateType.NO_BACKUP_DATA -> Icons.Default.Check
                            EmptyStateType.NO_NOTIFICATIONS -> Icons.Default.Check
                            EmptyStateType.LOADING_ERROR -> Icons.Default.Close
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (type) {
                            EmptyStateType.NO_EMPLOYEES -> "Add First Employee"
                            EmptyStateType.NO_SEARCH_RESULTS -> "Clear Search"
                            EmptyStateType.NO_ATTENDANCE_DATA -> "Mark Attendance"
                            EmptyStateType.NO_BACKUP_DATA -> "Create Backup"
                            EmptyStateType.NO_NOTIFICATIONS -> "Check Settings"
                            EmptyStateType.LOADING_ERROR -> "Try Again"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ModernLoadingState(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ModernPrimary, ModernPrimaryLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Loading",
                    modifier = Modifier
                        .size(32.dp)
                        .scale(1.2f),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Please wait while we fetch your data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
} 