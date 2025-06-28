package com.malikhain.employee_attendance_manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.malikhain.employee_attendance_manager.navigation.AppNavGraph
import com.malikhain.employee_attendance_manager.screens.LoginScreen
import com.malikhain.employee_attendance_manager.ui.theme.EmployeeattendancemanagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmployeeattendancemanagerTheme {
                AppNavGraph()
            }
        }
    }
}

// TODO: Add onboarding/tutorial for new users
// TODO: Add a settings/preferences page (e.g., notification settings, theme)
// TODO: Add a dark mode toggle if not already automatic
// TODO: Add notifications/reminders for marking attendance or other actions
// TODO: Implement offline support and sync status
// TODO: Ensure accessibility (screen reader, contrast, focus order)
// TODO: Track and display admin actions for audit purposes
