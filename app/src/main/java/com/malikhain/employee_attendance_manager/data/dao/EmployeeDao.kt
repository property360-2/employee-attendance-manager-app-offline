package com.malikhain.employee_attendance_manager.data.dao

import androidx.room.*
import com.malikhain.employee_attendance_manager.data.entities.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    @Query("DELETE FROM employees")
    suspend fun deleteAllEmployees()

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :query || '%' OR jobTitle LIKE '%' || :query || '%'")
    suspend fun searchEmployees(query: String): List<Employee>

    @Query("SELECT * FROM employees ORDER BY createdAt DESC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Int): Employee?
} 