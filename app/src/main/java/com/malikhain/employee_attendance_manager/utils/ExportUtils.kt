package com.malikhain.employee_attendance_manager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.malikhain.employee_attendance_manager.data.entities.Employee
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONObject
import org.json.JSONArray

object ExportUtils {
    
    fun exportEmployeesToCSV(context: Context, employees: List<Employee>): Uri? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "employees_$timestamp.csv"
            val file = File(context.cacheDir, fileName)
            
            FileWriter(file).use { writer ->
                // Write CSV header
                writer.append("Employee ID,Name,Job Title,Email,Phone,Address,Hire Date\n")
                
                // Write employee data
                employees.forEach { employee ->
                    writer.append("${employee.id},")
                    writer.append("\"${employee.name}\",")
                    writer.append("\"${employee.jobTitle}\",")
                    writer.append("\"${employee.email ?: ""}\",")
                    writer.append("\"${employee.phone ?: ""}\",")
                    writer.append("\"${employee.address ?: ""}\",")
                    writer.append("${employee.createdAt}\n")
                }
            }
            
            // Get content URI for sharing
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun exportAttendanceToCSV(context: Context, attendanceList: List<Attendance>, employees: List<Employee>): Uri? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "attendance_$timestamp.csv"
            val file = File(context.cacheDir, fileName)
            
            // Create employee map for quick lookup
            val employeeMap: Map<Int, Employee> = employees.associateBy { it.id }
            
            FileWriter(file).use { writer ->
                // Write CSV header
                writer.append("Date,Employee ID,Employee Name,Status\n")
                
                // Write attendance data
                attendanceList.forEach { attendance ->
                    val employee = employeeMap[attendance.employeeId]
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(attendance.date))
                    
                    writer.append("$date,")
                    writer.append("${attendance.employeeId},")
                    writer.append("\"${employee?.name ?: "Unknown"}\",")
                    writer.append("\"${attendance.status}\"\n")
                }
            }
            
            // Get content URI for sharing
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createBackupFile(context: Context, backupData: String): Uri? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "backup_$timestamp.json"
            val file = File(context.cacheDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write(backupData)
            }
            
            // Get content URI for sharing
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportToExcel(context: Context, employees: List<Employee>, attendance: List<Attendance>): Uri? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "attendance_report_$timestamp.csv" // Using CSV as Excel-compatible format
            val file = File(context.cacheDir, fileName)
            
            // Create employee map for quick lookup
            val employeeMap: Map<Int, Employee> = employees.associateBy { it.id }
            
            FileWriter(file).use { writer ->
                // Write comprehensive report header
                writer.append("Report Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
                writer.append("Total Employees: ${employees.size}\n")
                writer.append("Total Attendance Records: ${attendance.size}\n\n")
                
                // Write employee summary
                writer.append("Employee Summary\n")
                writer.append("Employee ID,Name,Job Title,Email,Total Days Present,Total Days Absent,Total Days Leave,Attendance Rate\n")
                
                employees.forEach { employee ->
                    val employeeAttendance = attendance.filter { it.employeeId == employee.id }
                    val presentDays = employeeAttendance.count { it.status == "Present" }
                    val absentDays = employeeAttendance.count { it.status == "Absent" }
                    val leaveDays = employeeAttendance.count { it.status == "Leave" }
                    val totalDays = employeeAttendance.size
                    val attendanceRate = if (totalDays > 0) {
                        String.format("%.1f", (presentDays.toDouble() / totalDays) * 100)
                    } else "0.0"
                    
                    writer.append("${employee.id},")
                    writer.append("\"${employee.name}\",")
                    writer.append("\"${employee.jobTitle}\",")
                    writer.append("\"${employee.email ?: ""}\",")
                    writer.append("$presentDays,")
                    writer.append("$absentDays,")
                    writer.append("$leaveDays,")
                    writer.append("${attendanceRate}%\n")
                }
                
                writer.append("\nDetailed Attendance Records\n")
                writer.append("Date,Employee ID,Employee Name,Status,Notes\n")
                
                // Write detailed attendance data
                attendance.forEach { att ->
                    val employee = employeeMap[att.employeeId]
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(att.date))
                    
                    writer.append("$date,")
                    writer.append("${att.employeeId},")
                    writer.append("\"${employee?.name ?: "Unknown"}\",")
                    writer.append("\"${att.status}\",")
                    writer.append("\"\"\n") // Notes column (empty for now)
                }
            }
            
            // Get content URI for sharing
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportMonthlyReport(context: Context, employees: List<Employee>, attendance: List<Attendance>, month: Int, year: Int): Uri? {
        return try {
            val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1)
                set(Calendar.YEAR, year)
            }.time)
            
            val fileName = "monthly_report_${monthName}_$year.csv"
            val file = File(context.cacheDir, fileName)
            
            // Filter attendance for the specified month
            val calendar = Calendar.getInstance()
            val monthlyAttendance = attendance.filter { att ->
                calendar.timeInMillis = att.date
                calendar.get(Calendar.MONTH) == month - 1 && calendar.get(Calendar.YEAR) == year
            }
            
            // Create employee map for quick lookup
            val employeeMap: Map<Int, Employee> = employees.associateBy { it.id }
            
            FileWriter(file).use { writer ->
                // Write monthly report header
                writer.append("Monthly Attendance Report - $monthName $year\n")
                writer.append("Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
                writer.append("Total Employees: ${employees.size}\n")
                writer.append("Total Working Days: ${monthlyAttendance.map { it.date }.distinct().size}\n\n")
                
                // Write employee summary for the month
                writer.append("Employee Monthly Summary\n")
                writer.append("Employee ID,Name,Job Title,Present Days,Absent Days,Leave Days,Working Days,Attendance Rate\n")
                
                employees.forEach { employee ->
                    val employeeAttendance = monthlyAttendance.filter { it.employeeId == employee.id }
                    val presentDays = employeeAttendance.count { it.status == "Present" }
                    val absentDays = employeeAttendance.count { it.status == "Absent" }
                    val leaveDays = employeeAttendance.count { it.status == "Leave" }
                    val workingDays = monthlyAttendance.map { it.date }.distinct().size
                    val attendanceRate = if (workingDays > 0) {
                        String.format("%.1f", (presentDays.toDouble() / workingDays) * 100)
                    } else "0.0"
                    
                    writer.append("${employee.id},")
                    writer.append("\"${employee.name}\",")
                    writer.append("\"${employee.jobTitle}\",")
                    writer.append("$presentDays,")
                    writer.append("$absentDays,")
                    writer.append("$leaveDays,")
                    writer.append("$workingDays,")
                    writer.append("${attendanceRate}%\n")
                }
            }
            
            // Get content URI for sharing
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareFile(context: Context, uri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Employee Attendance Data")
            putExtra(Intent.EXTRA_TEXT, "Please find the exported data attached.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun shareBackupFile(context: Context, uri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Employee Attendance Manager Backup")
            putExtra(Intent.EXTRA_TEXT, "Please find the backup file attached.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Backup via"))
    }
} 