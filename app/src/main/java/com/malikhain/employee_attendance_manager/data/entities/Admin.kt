package com.malikhain.employee_attendance_manager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admins")
data class Admin(
    @PrimaryKey(autoGenerate = true) val adminId: Int = 0,
    val username: String,
    val password: String,
    val email: String? = null,
    val failedLoginAttempts: Int = 0,
    val isLocked: Boolean = false,
    val lockoutUntil: Long = 0,
    val lastLoginTime: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

