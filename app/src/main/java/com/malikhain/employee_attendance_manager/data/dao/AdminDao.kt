package com.malikhain.employee_attendance_manager.data.dao

import androidx.room.*
import com.malikhain.employee_attendance_manager.data.entities.Admin
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {
    @Query("SELECT * FROM admins WHERE username = :username")
    suspend fun getAdminByUsername(username: String): Admin?

    @Query("SELECT * FROM admins WHERE adminId = :adminId")
    suspend fun getAdminById(adminId: Int): Admin?

    @Query("SELECT * FROM admins")
    fun getAllAdmins(): Flow<List<Admin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: Admin)

    @Update
    suspend fun updateAdmin(admin: Admin)

    @Delete
    suspend fun deleteAdmin(admin: Admin)

    @Query("UPDATE admins SET failedLoginAttempts = :attempts, isLocked = :isLocked, lockoutUntil = :lockoutUntil WHERE adminId = :adminId")
    suspend fun updateLoginAttempts(adminId: Int, attempts: Int, isLocked: Boolean, lockoutUntil: Long)

    @Query("UPDATE admins SET lastLoginTime = :loginTime, failedLoginAttempts = 0, isLocked = false, lockoutUntil = 0 WHERE adminId = :adminId")
    suspend fun updateSuccessfulLogin(adminId: Int, loginTime: Long)
}
