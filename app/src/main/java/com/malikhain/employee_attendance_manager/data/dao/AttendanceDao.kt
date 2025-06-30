package com.malikhain.employee_attendance_manager.data.dao

import androidx.room.*
import com.malikhain.employee_attendance_manager.data.entities.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance")
    suspend fun deleteAllAttendance()

    @Query("DELETE FROM attendance WHERE employeeId = :employeeId")
    suspend fun deleteAttendanceByEmployeeId(employeeId: Int)

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getAttendanceForEmployee(employeeId: Int): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date BETWEEN :startDate AND :endDate")
    fun getAttendanceInRange(startDate: Long, endDate: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date >= :startOfDay AND date <= :endOfDay")
    fun getTodaysAttendance(startOfDay: Long, endOfDay: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId AND date >= :startOfDay AND date <= :endOfDay LIMIT 1")
    suspend fun getAttendanceByDate(employeeId: Int, startOfDay: Long, endOfDay: Long): Attendance?

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    suspend fun getAllAttendance(): List<Attendance>

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId ORDER BY date DESC")
    suspend fun getAttendanceByEmployeeId(employeeId: Int): List<Attendance>
} 