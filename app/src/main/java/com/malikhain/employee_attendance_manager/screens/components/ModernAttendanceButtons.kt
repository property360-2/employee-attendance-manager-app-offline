package com.malikhain.employee_attendance_manager.screens.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.malikhain.employee_attendance_manager.ui.theme.*

data class AttendanceOption(
    val status: String,
    val icon: ImageVector,
    val color: Color,
    val backgroundColor: Color,
    val selectedColor: Color,
    val selectedBackgroundColor: Color
)

@Composable
fun ModernAttendanceButtons(
    currentStatus: String?,
    onMarkAttendance: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    val attendanceOptions = listOf(
        AttendanceOption(
            status = "Present",
            icon = Icons.Default.Check,
            color = ModernPresentColor,
            backgroundColor = ModernPresentContainer,
            selectedColor = Color.White,
            selectedBackgroundColor = ModernPresentColor
        ),
        AttendanceOption(
            status = "Absent",
            icon = Icons.Default.Close,
            color = ModernAbsentColor,
            backgroundColor = ModernAbsentContainer,
            selectedColor = Color.White,
            selectedBackgroundColor = ModernAbsentColor
        ),
        AttendanceOption(
            status = "Leave",
            icon = Icons.Default.Info,
            color = ModernLeaveColor,
            backgroundColor = ModernLeaveContainer,
            selectedColor = Color.White,
            selectedBackgroundColor = ModernLeaveColor
        )
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        attendanceOptions.forEach { option ->
            ModernAttendanceButton(
                option = option,
                isSelected = currentStatus == option.status,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onMarkAttendance(option.status)
                }
            )
        }
    }
}

@Composable
private fun ModernAttendanceButton(
    option: AttendanceOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 150, easing = EaseOutBack),
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) option.selectedBackgroundColor else option.backgroundColor,
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColor"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) option.selectedColor else option.color,
        animationSpec = tween(durationMillis = 200),
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = option.status,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )
        
        // Selection indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(iconColor)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun ModernAttendanceButtonCompact(
    currentStatus: String?,
    onMarkAttendance: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    val attendanceOptions = listOf(
        AttendanceOption(
            status = "Present",
            icon = Icons.Default.Check,
            color = ModernPresentColor,
            backgroundColor = ModernPresentContainer,
            selectedColor = Color.White,
            selectedBackgroundColor = ModernPresentColor
        ),
        AttendanceOption(
            status = "Absent",
            icon = Icons.Default.Close,
            color = ModernAbsentColor,
            backgroundColor = ModernAbsentContainer,
            selectedColor = Color.White,
            selectedBackgroundColor = ModernAbsentColor
        ),
        AttendanceOption(
            status = "Leave",
            icon = Icons.Default.Info,
            color = ModernLeaveColor,
            backgroundColor = ModernLeaveContainer,
            selectedColor = Color.White,
            selectedBackgroundColor = ModernLeaveColor
        )
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            attendanceOptions.forEach { option ->
                ModernAttendanceButton(
                    option = option,
                    isSelected = currentStatus == option.status,
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onMarkAttendance(option.status)
                    }
                )
            }
        }
    }
}

@Composable
fun ModernAttendanceChip(
    status: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    val (icon, color, backgroundColor) = when (status) {
        "Present" -> Triple(Icons.Default.Check, ModernPresentColor, ModernPresentContainer)
        "Absent" -> Triple(Icons.Default.Close, ModernAbsentColor, ModernAbsentContainer)
        "Leave" -> Triple(Icons.Default.Info, ModernLeaveColor, ModernLeaveContainer)
        else -> Triple(Icons.Default.Info, ModernNeutral500, ModernNeutral100)
    }
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable(onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            
            Text(
                text = status,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = color
            )
        }
    }
}

@Composable
fun ModernAttendanceStatusIndicator(
    status: String?,
    modifier: Modifier = Modifier
) {
    val (icon, color, text) = when (status) {
        "Present" -> Triple(Icons.Default.Check, ModernPresentColor, "Present")
        "Absent" -> Triple(Icons.Default.Close, ModernAbsentColor, "Absent")
        "Leave" -> Triple(Icons.Default.Info, ModernLeaveColor, "On Leave")
        else -> Triple(Icons.Default.Info, ModernNeutral500, "Not Marked")
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(14.dp),
            tint = color
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = color
        )
    }
} 