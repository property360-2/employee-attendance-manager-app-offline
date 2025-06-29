package com.malikhain.employee_attendance_manager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.malikhain.employee_attendance_manager.data.dao.AdminDao
import com.malikhain.employee_attendance_manager.data.dao.EmployeeDao
import com.malikhain.employee_attendance_manager.data.dao.AttendanceDao
import com.malikhain.employee_attendance_manager.data.entities.Admin
import com.malikhain.employee_attendance_manager.data.entities.Employee
import com.malikhain.employee_attendance_manager.data.entities.Attendance

@Database(
    entities = [Admin::class, Employee::class, Attendance::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun adminDao(): AdminDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_db"
                )
                .addMigrations(MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to admins table
                database.execSQL("ALTER TABLE admins ADD COLUMN email TEXT")
                database.execSQL("ALTER TABLE admins ADD COLUMN failedLoginAttempts INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE admins ADD COLUMN isLocked INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE admins ADD COLUMN lockoutUntil INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE admins ADD COLUMN lastLoginTime INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
