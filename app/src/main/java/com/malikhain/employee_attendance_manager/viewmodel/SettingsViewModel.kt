package com.malikhain.employee_attendance_manager.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malikhain.employee_attendance_manager.data.dao.EmployeeDao
import com.malikhain.employee_attendance_manager.data.dao.AttendanceDao
import com.malikhain.employee_attendance_manager.data.entities.Employee
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import com.malikhain.employee_attendance_manager.data.PreferencesManager
import com.malikhain.employee_attendance_manager.utils.ExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val uri: Uri, val fileName: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val employeeDao: EmployeeDao,
    private val attendanceDao: AttendanceDao
) : ViewModel() {

    val darkMode: StateFlow<Boolean> = preferencesManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val notifications: StateFlow<Boolean> = preferencesManager.isNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val biometricAuth: StateFlow<Boolean> = preferencesManager.isBiometricAuthEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val autoBackup: StateFlow<Boolean> = preferencesManager.isAutoBackupEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val lastBackupTime: StateFlow<Long> = preferencesManager.lastBackupTime
        .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }

    fun setBiometricAuthEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setBiometricAuthEnabled(enabled)
        }
    }

    fun setAutoBackupEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoBackupEnabled(enabled)
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            try {
                // Create a simple backup by exporting all data
                val employees = employeeDao.getAllEmployees().first()
                val attendance = attendanceDao.getAllAttendance()
                
                // For now, just update the last backup time
                // In a real app, you'd save to cloud storage or local backup
                preferencesManager.setLastBackupTime(System.currentTimeMillis())
            } catch (e: Exception) {
                // Handle backup error
            }
        }
    }

    fun exportEmployees(context: Context) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            try {
                val employees = employeeDao.getAllEmployees().first()
                val uri = ExportUtils.exportEmployeesToCSV(context, employees)
                
                if (uri != null) {
                    val fileName = "employees_${System.currentTimeMillis()}.csv"
                    _exportState.value = ExportState.Success(uri, fileName)
                } else {
                    _exportState.value = ExportState.Error("Failed to export employees data")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Export failed: ${e.message}")
            }
        }
    }
    
    fun exportAttendance(context: Context) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            try {
                val employees = employeeDao.getAllEmployees().first()
                val attendance = attendanceDao.getAllAttendance()
                val uri = ExportUtils.exportAttendanceToCSV(context, attendance, employees)
                
                if (uri != null) {
                    val fileName = "attendance_${System.currentTimeMillis()}.csv"
                    _exportState.value = ExportState.Success(uri, fileName)
                } else {
                    _exportState.value = ExportState.Error("Failed to export attendance data")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Export failed: ${e.message}")
            }
        }
    }

    fun importData() {
        viewModelScope.launch {
            // TODO: Implement data import logic
            // This would involve reading CSV files and importing data
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                // Clear all data from database
                attendanceDao.deleteAllAttendance()
                employeeDao.deleteAllEmployees()
                preferencesManager.clearAllPreferences()
            } catch (e: Exception) {
                // Handle clearing error
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesManager.setSessionToken(null)
            preferencesManager.setSessionExpiry(0L)
        }
    }
    
    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }
} 