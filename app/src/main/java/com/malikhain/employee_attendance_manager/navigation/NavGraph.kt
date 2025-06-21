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
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Home.route) {
            val viewModel = hiltViewModel<com.malikhain.employee_attendance_manager.viewmodel.EmployeeViewModel>()
            HomeScreen(navController, viewModel)
        }
        composable(Screen.AttendanceView.route) { AttendanceViewScreen(navController) }
        composable(Screen.AddEmployee.route) { AddEmployeeScreen(navController) }
        composable("edit/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            EditEmployeeScreen(navController, id)
        }
        composable("details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            EmployeeDetailsScreen(navController, id)
        }
    }
}
