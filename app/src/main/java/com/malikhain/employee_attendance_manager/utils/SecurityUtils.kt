package com.malikhain.employee_attendance_manager.utils

import org.mindrot.jbcrypt.BCrypt
import java.util.regex.Pattern

object SecurityUtils {
    
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MAX_LOGIN_ATTEMPTS = 5
    private const val LOCKOUT_DURATION_MS = 15 * 60 * 1000 // 15 minutes
    
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
    
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
    
    fun validatePassword(password: String): PasswordValidationResult {
        if (password.length < MIN_PASSWORD_LENGTH) {
            return PasswordValidationResult.Error("Password must be at least $MIN_PASSWORD_LENGTH characters long")
        }
        
        if (!Pattern.compile(".*[A-Z].*").matcher(password).find()) {
            return PasswordValidationResult.Error("Password must contain at least one uppercase letter")
        }
        
        if (!Pattern.compile(".*[a-z].*").matcher(password).find()) {
            return PasswordValidationResult.Error("Password must contain at least one lowercase letter")
        }
        
        if (!Pattern.compile(".*\\d.*").matcher(password).find()) {
            return PasswordValidationResult.Error("Password must contain at least one number")
        }
        
        if (!Pattern.compile(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*").matcher(password).find()) {
            return PasswordValidationResult.Error("Password must contain at least one special character")
        }
        
        return PasswordValidationResult.Success
    }
    
    fun validateUsername(username: String): UsernameValidationResult {
        if (username.length < 3) {
            return UsernameValidationResult.Error("Username must be at least 3 characters long")
        }
        
        if (!Pattern.compile("^[a-zA-Z0-9_]+$").matcher(username).find()) {
            return UsernameValidationResult.Error("Username can only contain letters, numbers, and underscores")
        }
        
        return UsernameValidationResult.Success
    }
    
    fun validateEmail(email: String): EmailValidationResult {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )
        
        return if (emailPattern.matcher(email).matches()) {
            EmailValidationResult.Success
        } else {
            EmailValidationResult.Error("Please enter a valid email address")
        }
    }
    
    fun shouldLockAccount(failedAttempts: Int): Boolean {
        return failedAttempts >= MAX_LOGIN_ATTEMPTS
    }
    
    fun getLockoutEndTime(): Long {
        return System.currentTimeMillis() + LOCKOUT_DURATION_MS
    }
    
    fun isAccountLocked(lockoutUntil: Long): Boolean {
        return System.currentTimeMillis() < lockoutUntil
    }
    
    fun getRemainingLockoutTime(lockoutUntil: Long): Long {
        return maxOf(0, lockoutUntil - System.currentTimeMillis())
    }
}

sealed class PasswordValidationResult {
    object Success : PasswordValidationResult()
    data class Error(val message: String) : PasswordValidationResult()
}

sealed class UsernameValidationResult {
    object Success : UsernameValidationResult()
    data class Error(val message: String) : UsernameValidationResult()
}

sealed class EmailValidationResult {
    object Success : EmailValidationResult()
    data class Error(val message: String) : EmailValidationResult()
} 