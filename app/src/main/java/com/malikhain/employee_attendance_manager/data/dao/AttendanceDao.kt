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

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getAttendanceForEmployee(employeeId: Int): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date BETWEEN :startDate AND :endDate")
    fun getAttendanceInRange(startDate: Long, endDate: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date >= :startOfDay AND date <= :endOfDay")
    fun getTodaysAttendance(startOfDay: Long, endOfDay: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date AND employeeId = :employeeId LIMIT 1")
    suspend fun getAttendanceByDate(employeeId: Int, date: Long): Attendance?
} 