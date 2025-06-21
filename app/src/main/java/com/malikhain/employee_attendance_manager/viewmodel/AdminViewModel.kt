package com.malikhain.employee_attendance_manager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malikhain.employee_attendance_manager.data.AppDatabase
import com.malikhain.employee_attendance_manager.data.entities.Admin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).adminDao()

    fun register(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val newAdmin = Admin(
                username = username,
                password = password
            )
            dao.insertAdmin(newAdmin)
            onSuccess()
        }
    }


    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val admin = dao.login(username, password)
            onResult(admin != null)
        }
    }

    fun hasAdmin(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            onResult(dao.hasAdmin() > 0)
        }
    }
}
