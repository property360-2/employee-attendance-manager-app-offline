package com.malikhain.employee_attendance_manager.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malikhain.employee_attendance_manager.data.dao.AttendanceDao
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceDao: AttendanceDao
) : ViewModel() {
    private val _allAttendance = MutableStateFlow<List<Attendance>>(emptyList())
    val allAttendance: StateFlow<List<Attendance>> = _allAttendance

    fun attendanceRecords(employeeId: Int): StateFlow<List<Attendance>> =
        attendanceDao.getAttendanceForEmployee(employeeId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun markAttendance(attendance: Attendance) {
        viewModelScope.launch { attendanceDao.insertAttendance(attendance) }
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

    fun loadAllAttendance() {
        viewModelScope.launch {
            try {
                val attendance = attendanceDao.getAllAttendance()
                _allAttendance.value = attendance
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 