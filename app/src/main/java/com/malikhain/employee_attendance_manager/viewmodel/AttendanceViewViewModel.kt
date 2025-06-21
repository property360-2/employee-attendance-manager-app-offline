package com.malikhain.employee_attendance_manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malikhain.employee_attendance_manager.data.dao.AttendanceDao
import com.malikhain.employee_attendance_manager.data.dao.EmployeeDao
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import com.malikhain.employee_attendance_manager.data.entities.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AttendanceViewViewModel @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val attendanceDao: AttendanceDao
) : ViewModel() {

    val employees: StateFlow<List<Employee>> = employeeDao.getAllEmployees()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _filterType = MutableStateFlow("Day")
    val filterType: StateFlow<String> = _filterType

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredAttendance: StateFlow<List<Attendance>> = _filterType.flatMapLatest { filter ->
        val (start, end) = getDateRange(filter)
        attendanceDao.getAttendanceInRange(start, end)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun getDateRange(filterType: String): Pair<Long, Long> {
        val today = LocalDate.now()
        val start: LocalDate
        val end: LocalDate

        when (filterType) {
            "Day" -> {
                start = today
                end = today
            }
            "Week" -> {
                start = today.with(org.threeten.bp.DayOfWeek.MONDAY)
                end = start.plusDays(6)
            }
            "Month" -> {
                start = today.withDayOfMonth(1)
                end = today.withDayOfMonth(today.lengthOfMonth())
            }
            else -> {
                start = today
                end = today
            }
        }

        val startMillis = start.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endMillis = end.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli()

        return Pair(startMillis, endMillis)
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
} 