package com.malikhain.employee_attendance_manager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.malikhain.employee_attendance_manager.screens.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.malikhain.employee_attendance_manager.data.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    preferencesManager: PreferencesManager
) {
    val isOnboardingCompleted by preferencesManager.isOnboardingCompleted.collectAsState(initial = false)
    
    LaunchedEffect(isOnboardingCompleted) {
        if (isOnboardingCompleted) {
            navController.navigate("login") {
                popUpTo("onboarding") { inclusive = true }
            }
        }
    }
    
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") { 
            OnboardingScreen(navController, preferencesManager) 
        }
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
        composable("terms_and_conditions") {
            TermsAndConditionsScreen(navController)
        }
        composable("privacy_policy") {
            PrivacyPolicyScreen(navController)
        }
        composable("help_support") {
            HelpSupportScreen(navController)
        }
    }
}
