package com.malikhain.employee_attendance_manager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malikhain.employee_attendance_manager.data.entities.Admin

@Dao
interface AdminDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: Admin): Long

    @Query("SELECT * FROM admins WHERE username = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): Admin?

    @Query("SELECT * FROM admins WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): Admin?

    @Query("SELECT COUNT(*) FROM admins")
    suspend fun hasAdmin(): Int
}
