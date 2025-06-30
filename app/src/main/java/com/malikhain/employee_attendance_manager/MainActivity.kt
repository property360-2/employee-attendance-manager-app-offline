package com.malikhain.employee_attendance_manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.malikhain.employee_attendance_manager.navigation.AppNavGraph
import com.malikhain.employee_attendance_manager.screens.LoginScreen
import com.malikhain.employee_attendance_manager.ui.theme.EmployeeattendancemanagerTheme
import com.malikhain.employee_attendance_manager.utils.NotificationUtils
import com.malikhain.employee_attendance_manager.data.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize notification channel
        NotificationUtils.createNotificationChannel(this)
        
        setContent {
            val themeMode by preferencesManager.themeMode.collectAsStateWithLifecycle(initialValue = "system")
            
            EmployeeattendancemanagerTheme(themeMode = themeMode) {
                AppNavGraph(preferencesManager = preferencesManager)
            }
        }
    }
}
