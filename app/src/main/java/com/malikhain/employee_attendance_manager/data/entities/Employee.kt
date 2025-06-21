package com.malikhain.employee_attendance_manager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val jobTitle: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    val createdAt: Long = System.currentTimeMillis()
) 