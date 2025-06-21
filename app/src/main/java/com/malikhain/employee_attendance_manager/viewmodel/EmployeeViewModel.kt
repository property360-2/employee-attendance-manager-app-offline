package com.malikhain.employee_attendance_manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malikhain.employee_attendance_manager.data.dao.EmployeeDao
import com.malikhain.employee_attendance_manager.data.entities.Employee
import com.malikhain.employee_attendance_manager.data.dao.AttendanceDao
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class EmployeeWithAttendance(
    val employee: Employee,
    val attendanceStatus: String?
)

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val attendanceDao: AttendanceDao
) : ViewModel() {

    private val _employeesWithAttendance = MutableStateFlow<List<EmployeeWithAttendance>>(emptyList())
    val employeesWithAttendance: StateFlow<List<EmployeeWithAttendance>> = _employeesWithAttendance

    val employees: StateFlow<List<Employee>> = employeeDao.getAllEmployees()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            employeeDao.getAllEmployees().combine(attendanceDao.getTodaysAttendance(startOfDay, endOfDay)) { employees, todaysAttendance ->
                employees.map { employee ->
                    val attendance = todaysAttendance.find { it.employeeId == employee.id }
                    EmployeeWithAttendance(employee, attendance?.status)
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
            .collect { _employeesWithAttendance.value = it }
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch { employeeDao.insertEmployee(employee) }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch { employeeDao.updateEmployee(employee) }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch { employeeDao.deleteEmployee(employee) }
    }

    fun editAttendance(employeeId: Int, status: String, date: Long) {
        viewModelScope.launch {
            val existing = attendanceDao.getAttendanceByDate(employeeId, date)
            if (existing != null) {
                attendanceDao.updateAttendance(existing.copy(status = status))
            } else {
                attendanceDao.insertAttendance(
                    Attendance(employeeId = employeeId, status = status, date = date)
                )
            }
        }
    }

    fun markAttendance(employeeId: Int, status: String) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        editAttendance(employeeId, status, today)
    }
} 