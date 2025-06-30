package com.malikhain.employee_attendance_manager.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malikhain.employee_attendance_manager.data.dao.AdminDao
import com.malikhain.employee_attendance_manager.data.entities.Admin
import com.malikhain.employee_attendance_manager.data.PreferencesManager
import com.malikhain.employee_attendance_manager.utils.SecurityUtils
import com.malikhain.employee_attendance_manager.utils.PasswordValidationResult
import com.malikhain.employee_attendance_manager.utils.UsernameValidationResult
import com.malikhain.employee_attendance_manager.utils.EmailValidationResult
import com.malikhain.employee_attendance_manager.utils.BiometricUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
    data class AccountLocked(val remainingTime: Long) : LoginState()
    object BiometricRequired : LoginState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val adminDao: AdminDao,
    private val preferencesManager: PreferencesManager,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    private val _biometricAvailable = MutableStateFlow(false)
    val biometricAvailable: StateFlow<Boolean> = _biometricAvailable

    private val _rememberUsername = MutableStateFlow(false)
    val rememberUsername: StateFlow<Boolean> = _rememberUsername

    private val _savedUsername = MutableStateFlow<String?>(null)
    val savedUsername: StateFlow<String?> = _savedUsername

    init {
        checkBiometricAvailability()
        loadRememberUsernameSettings()
        checkSessionToken()
    }

    private fun checkBiometricAvailability() {
        _biometricAvailable.value = BiometricUtils.isBiometricAvailable(context)
    }

    private fun loadRememberUsernameSettings() {
        viewModelScope.launch {
            preferencesManager.shouldRememberUsername.collect { remember ->
                _rememberUsername.value = remember
            }
        }
        viewModelScope.launch {
            preferencesManager.savedUsername.collect { username ->
                _savedUsername.value = username
            }
        }
    }

    private fun checkSessionToken() {
        viewModelScope.launch {
            preferencesManager.sessionToken.collect { token ->
                if (token != null) {
                    preferencesManager.sessionExpiry.collect { expiry ->
                        if (System.currentTimeMillis() < expiry) {
                            // Valid session exists, auto-login
                            _loginState.value = LoginState.Success
                        } else {
                            // Session expired, clear it
                            preferencesManager.setSessionToken(null)
                        }
                    }
                }
            }
        }
    }

    fun login(username: String, password: String, rememberMe: Boolean = false) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Please enter both username and password")
            return
        }

        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            try {
                val admin = adminDao.getAdminByUsername(username)
                
                if (admin == null) {
                    _loginState.value = LoginState.Error("Invalid username or password")
                    return@launch
                }

                // Check if account is locked
                if (SecurityUtils.isAccountLocked(admin.lockoutUntil)) {
                    val remainingTime = SecurityUtils.getRemainingLockoutTime(admin.lockoutUntil)
                    _loginState.value = LoginState.AccountLocked(remainingTime)
                    return@launch
                }

                // Verify password
                if (SecurityUtils.verifyPassword(password, admin.password)) {
                    // Successful login
                    adminDao.updateSuccessfulLogin(admin.adminId, System.currentTimeMillis())
                    
                    // Handle remember me
                    if (rememberMe) {
                        val sessionToken = UUID.randomUUID().toString()
                        val expiryTime = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000) // 30 days
                        preferencesManager.setSessionToken(sessionToken)
                        preferencesManager.setSessionExpiry(expiryTime)
                    }
                    
                    // Handle remember username
                    if (_rememberUsername.value) {
                        preferencesManager.setSavedUsername(username)
                    } else {
                        preferencesManager.setSavedUsername(null)
                    }
                    
                    _loginState.value = LoginState.Success
                } else {
                    // Failed login
                    val newFailedAttempts = admin.failedLoginAttempts + 1
                    val shouldLock = SecurityUtils.shouldLockAccount(newFailedAttempts)
                    val lockoutUntil = if (shouldLock) SecurityUtils.getLockoutEndTime() else 0L
                    
                    adminDao.updateLoginAttempts(admin.adminId, newFailedAttempts, shouldLock, lockoutUntil)
                    
                    if (shouldLock) {
                        _loginState.value = LoginState.AccountLocked(SecurityUtils.getLockoutEndTime())
                    } else {
                        val remainingAttempts = 5 - newFailedAttempts
                        _loginState.value = LoginState.Error("Invalid password. $remainingAttempts attempts remaining.")
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed. Please try again.")
            }
        }
    }

    fun loginWithBiometric() {
        _loginState.value = LoginState.BiometricRequired
    }

    fun handleBiometricSuccess() {
        viewModelScope.launch {
            // Check if we have a saved session
            preferencesManager.sessionToken.collect { token ->
                if (token != null) {
                    preferencesManager.sessionExpiry.collect { expiry ->
                        if (System.currentTimeMillis() < expiry) {
                            _loginState.value = LoginState.Success
                        } else {
                            preferencesManager.setSessionToken(null)
                            _loginState.value = LoginState.Error("Session expired. Please login with credentials.")
                        }
                    }
                } else {
                    _loginState.value = LoginState.Error("No saved session found. Please login with credentials.")
                }
            }
        }
    }

    fun setRememberUsername(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setRememberUsername(enabled)
            _rememberUsername.value = enabled
            if (!enabled) {
                preferencesManager.setSavedUsername(null)
                _savedUsername.value = null
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesManager.setSessionToken(null)
            preferencesManager.setSessionExpiry(0L)
            _loginState.value = LoginState.Idle
        }
    }

    fun register(username: String, password: String, email: String? = null) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            
            // Validate username
            val usernameValidation = SecurityUtils.validateUsername(username)
            if (usernameValidation is UsernameValidationResult.Error) {
                _registrationState.value = RegistrationState.Error(usernameValidation.message)
                return@launch
            }
            
            // Validate password
            val passwordValidation = SecurityUtils.validatePassword(password)
            if (passwordValidation is PasswordValidationResult.Error) {
                _registrationState.value = RegistrationState.Error(passwordValidation.message)
                return@launch
            }
            
            // Validate email if provided
            if (email != null && email.isNotBlank()) {
                val emailValidation = SecurityUtils.validateEmail(email)
                if (emailValidation is EmailValidationResult.Error) {
                    _registrationState.value = RegistrationState.Error(emailValidation.message)
                    return@launch
                }
            }
            
            try {
                // Check if username already exists
                val existingAdmin = adminDao.getAdminByUsername(username)
                if (existingAdmin != null) {
                    _registrationState.value = RegistrationState.Error("Username already exists.")
                    return@launch
                }
                
                // Hash password
                val hashedPassword = SecurityUtils.hashPassword(password)
                
                // Create new admin
                val newAdmin = Admin(
                    username = username,
                    password = hashedPassword,
                    email = email
                )
                
                adminDao.insertAdmin(newAdmin)
                _registrationState.value = RegistrationState.Success
                
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error("Registration failed. Please try again.")
            }
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.Idle
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun changePassword(adminId: Int, currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val admin = adminDao.getAdminById(adminId)
                if (admin == null) {
                    onError("Admin not found")
                    return@launch
                }
                
                // Verify current password
                if (!SecurityUtils.verifyPassword(currentPassword, admin.password)) {
                    onError("Current password is incorrect")
                    return@launch
                }
                
                // Validate new password
                val passwordValidation = SecurityUtils.validatePassword(newPassword)
                if (passwordValidation is com.malikhain.employee_attendance_manager.utils.PasswordValidationResult.Error) {
                    onError(passwordValidation.message)
                    return@launch
                }
                
                // Hash new password
                val hashedNewPassword = SecurityUtils.hashPassword(newPassword)
                
                // Update password
                adminDao.updateAdmin(admin.copy(password = hashedNewPassword))
                
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to change password: ${e.message}")
            }
        }
    }
} 