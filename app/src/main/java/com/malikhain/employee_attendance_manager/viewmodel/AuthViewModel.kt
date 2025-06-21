package com.malikhain.employee_attendance_manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malikhain.employee_attendance_manager.data.dao.AdminDao
import com.malikhain.employee_attendance_manager.data.entities.Admin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(private val adminDao: AdminDao) : ViewModel() {
    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val admin = adminDao.getAdminByUsername(username)
            _loginState.value = admin?.password == password
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            if (adminDao.getAdminByUsername(username) != null) {
                _registrationState.value = RegistrationState.Error("Username already exists.")
            } else {
                val admin = Admin(username = username, password = password)
                adminDao.insertAdmin(admin)
                _registrationState.value = RegistrationState.Success
            }
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.Idle
    }
} 