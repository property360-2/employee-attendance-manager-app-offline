package com.malikhain.employee_attendance_manager.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AttendanceView : Screen("attendanceView")
    object AddEmployee : Screen("add")
    data class EditEmployee(val id: Int) : Screen("edit/{id}") {
        companion object { fun createRoute(id: Int) = "edit/$id" }
    }
    data class EmployeeDetails(val id: Int) : Screen("details/{id}") {
        companion object { fun createRoute(id: Int) = "details/$id" }
    }
}
