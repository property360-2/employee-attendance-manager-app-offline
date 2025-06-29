package com.malikhain.employee_attendance_manager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.malikhain.employee_attendance_manager.screens.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") { OnboardingScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.AttendanceView.route) { AttendanceViewScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.AddEmployee.route) { AddEmployeeScreen(navController) }
        composable("edit/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            EditEmployeeScreen(navController, id)
        }
        composable("details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            EmployeeDetailsScreen(navController, id)
        }
        composable("change_password") {
            ChangePasswordScreen(navController)
        }
        composable("analytics") {
            AnalyticsScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}
