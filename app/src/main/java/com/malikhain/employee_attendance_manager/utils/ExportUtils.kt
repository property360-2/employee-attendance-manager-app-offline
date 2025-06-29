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
                writer.append("Date,Employee ID,Employee Name,Status,Timestamp\n")
                
                // Write attendance data
                attendanceList.forEach { attendance ->
                    val employee = employeeMap[attendance.employeeId]
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(attendance.date))
                    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(attendance.date))
                    
                    writer.append("$date,")
                    writer.append("${attendance.employeeId},")
                    writer.append("\"${employee?.name ?: "Unknown"}\",")
                    writer.append("\"${attendance.status}\",")
                    writer.append("$time\n")
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
} 