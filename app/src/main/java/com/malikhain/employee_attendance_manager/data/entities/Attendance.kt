package com.malikhain.employee_attendance_manager.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import com.malikhain.employee_attendance_manager.data.entities.Employee

@Entity(
    tableName = "attendance",
    foreignKeys = [ForeignKey(
        entity = Employee::class,
        parentColumns = ["id"],
        childColumns = ["employeeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["employeeId"])]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true) val attendanceId: Int = 0,
    val employeeId: Int,
    val date: Long,
    val status: String // "Present", "Absent", "Leave"
) 