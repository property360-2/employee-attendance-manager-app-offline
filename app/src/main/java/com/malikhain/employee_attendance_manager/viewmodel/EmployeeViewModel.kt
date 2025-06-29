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

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees

    private val _employeesWithAttendance = MutableStateFlow<List<EmployeeWithAttendance>>(emptyList())
    val employeesWithAttendance: StateFlow<List<EmployeeWithAttendance>> = _employeesWithAttendance

    private val _employeeAttendance = MutableStateFlow<List<Attendance>>(emptyList())
    val employeeAttendance: StateFlow<List<Attendance>> = _employeeAttendance

    init {
        loadEmployees()
        loadEmployeesWithAttendance()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            try {
                val allEmployees = employeeDao.getAllEmployees().first()
                _employees.value = allEmployees
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadEmployeesWithAttendance() {
        viewModelScope.launch {
            try {
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

                val employees = employeeDao.getAllEmployees().first()
                val todaysAttendance = attendanceDao.getTodaysAttendance(startOfDay, endOfDay).first()
                
                val employeesWithAttendance = employees.map { employee ->
                    val attendance = todaysAttendance.find { it.employeeId == employee.id }
                    EmployeeWithAttendance(employee, attendance?.status)
                }
                _employeesWithAttendance.value = employeesWithAttendance
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch { employeeDao.insertEmployee(employee) }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch { employeeDao.updateEmployee(employee) }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                // Delete employee's attendance records first
                attendanceDao.deleteAttendanceByEmployeeId(employee.id)
                // Then delete the employee
                employeeDao.deleteEmployee(employee)
                // Refresh the employee list
                loadEmployees()
            } catch (e: Exception) {
                // Handle deletion error
            }
        }
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

    fun searchEmployees(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadEmployees()
            } else {
                try {
                    val results = employeeDao.searchEmployees(query)
                    _employees.value = results
                } catch (e: Exception) {
                    // Handle search error
                }
            }
        }
    }

    fun getEmployee(employeeId: Int): StateFlow<Employee?> {
        return flow {
            emit(employeeDao.getEmployeeById(employeeId))
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    }
    
    fun getEmployeeAttendance(employeeId: Int): StateFlow<List<Attendance>> {
        return flow {
            emit(attendanceDao.getAttendanceByEmployeeId(employeeId))
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
    
    fun loadEmployeeAttendance(employeeId: Int) {
        viewModelScope.launch {
            try {
                val attendance = attendanceDao.getAttendanceByEmployeeId(employeeId)
                _employeeAttendance.value = attendance
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 