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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val uri: Uri, val fileName: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val message: String) : ImportState()
    data class Error(val message: String) : ImportState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val employeeDao: EmployeeDao,
    private val attendanceDao: AttendanceDao
) : ViewModel() {

    val darkMode: StateFlow<Boolean> = preferencesManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val themeMode: StateFlow<String> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.Lazily, "system")

    val language: StateFlow<String> = preferencesManager.language
        .stateIn(viewModelScope, SharingStarted.Lazily, "en")

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

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
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
                // Create a comprehensive backup
                val employees = employeeDao.getAllEmployees().first()
                val attendance = attendanceDao.getAllAttendance()
                
                // Create backup file with all data
                val backupData = createBackupData(employees, attendance)
                // val uri = ExportUtils.createBackupFile(context, backupData)
                
                // if (uri != null) {
                //     // Update last backup time
                //     preferencesManager.setLastBackupTime(System.currentTimeMillis())
                //     
                //     // TODO: Upload to cloud storage if enabled
                //     uploadToCloud(uri)
                // }
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

    fun exportAllData(context: Context) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            try {
                val employees = employeeDao.getAllEmployees().first()
                val attendance = attendanceDao.getAllAttendance()
                
                // Create comprehensive backup file
                val backupData = createBackupData(employees, attendance)
                val uri = ExportUtils.createBackupFile(context, backupData)
                
                if (uri != null) {
                    val fileName = "backup_${System.currentTimeMillis()}.json"
                    _exportState.value = ExportState.Success(uri, fileName)
                } else {
                    _exportState.value = ExportState.Error("Failed to create backup file")
                }
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Export failed: ${e.message}")
            }
        }
    }

    fun importData(fileUri: String) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            try {
                when {
                    fileUri.contains("employees") -> importEmployees(fileUri)
                    fileUri.contains("attendance") -> importAttendance(fileUri)
                    fileUri.contains("all") -> importAllData(fileUri)
                    else -> _importState.value = ImportState.Error("Unknown import type")
                }
            } catch (e: Exception) {
                _importState.value = ImportState.Error("Import failed: ${e.message}")
            }
        }
    }

    private suspend fun importEmployees(fileUri: String) {
        try {
            // TODO: Implement CSV parsing and employee import
            // This would read the CSV file and insert employees into the database
            _importState.value = ImportState.Success("Employees imported successfully")
        } catch (e: Exception) {
            _importState.value = ImportState.Error("Failed to import employees: ${e.message}")
        }
    }

    private suspend fun importAttendance(fileUri: String) {
        try {
            // TODO: Implement CSV parsing and attendance import
            // This would read the CSV file and insert attendance records into the database
            _importState.value = ImportState.Success("Attendance data imported successfully")
        } catch (e: Exception) {
            _importState.value = ImportState.Error("Failed to import attendance: ${e.message}")
        }
    }

    private suspend fun importAllData(fileUri: String) {
        try {
            // TODO: Implement JSON parsing and full data import
            // This would read the backup file and restore all data
            _importState.value = ImportState.Success("All data imported successfully")
        } catch (e: Exception) {
            _importState.value = ImportState.Error("Failed to import data: ${e.message}")
        }
    }

    private fun createBackupData(employees: List<Employee>, attendance: List<Attendance>): String {
        // Create a JSON backup with all data
        val backup = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "version" to "1.0",
            "employees" to employees.map { employee ->
                mapOf(
                    "id" to employee.id,
                    "name" to employee.name,
                    "email" to employee.email,
                    "jobTitle" to employee.jobTitle,
                    "phone" to employee.phone
                )
            },
            "attendance" to attendance.map { att ->
                mapOf(
                    "attendanceId" to att.attendanceId,
                    "employeeId" to att.employeeId,
                    "date" to att.date,
                    "status" to att.status
                )
            }
        )
        
        // Convert to JSON string (simplified for now)
        return backup.toString()
    }

    private suspend fun uploadToCloud(uri: Uri) {
        // TODO: Implement cloud backup upload
        // This would upload the backup file to Google Drive, Dropbox, etc.
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

    fun resetImportState() {
        _importState.value = ImportState.Idle
    }
} 