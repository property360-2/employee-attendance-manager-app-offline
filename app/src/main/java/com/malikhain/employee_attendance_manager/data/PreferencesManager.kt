package com.malikhain.employee_attendance_manager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val BIOMETRIC_AUTH_ENABLED = booleanPreferencesKey("biometric_auth_enabled")
        val AUTO_BACKUP_ENABLED = booleanPreferencesKey("auto_backup_enabled")
        val LAST_BACKUP_TIME = longPreferencesKey("last_backup_time")
        val REMEMBER_USERNAME = booleanPreferencesKey("remember_username")
        val SAVED_USERNAME = stringPreferencesKey("saved_username")
        val SESSION_TOKEN = stringPreferencesKey("session_token")
        val SESSION_EXPIRY = longPreferencesKey("session_expiry")
    }
    
    // Dark Mode
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
    
    // Notifications
    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    // Biometric Authentication
    val isBiometricAuthEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.BIOMETRIC_AUTH_ENABLED] ?: false
    }
    
    suspend fun setBiometricAuthEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_AUTH_ENABLED] = enabled
        }
    }
    
    // Auto Backup
    val isAutoBackupEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTO_BACKUP_ENABLED] ?: true
    }
    
    suspend fun setAutoBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_BACKUP_ENABLED] = enabled
        }
    }
    
    // Last Backup Time
    val lastBackupTime: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_BACKUP_TIME] ?: 0L
    }
    
    suspend fun setLastBackupTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_BACKUP_TIME] = timestamp
        }
    }
    
    // Remember Username
    val shouldRememberUsername: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REMEMBER_USERNAME] ?: false
    }
    
    suspend fun setRememberUsername(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMEMBER_USERNAME] = enabled
        }
    }
    
    // Saved Username
    val savedUsername: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SAVED_USERNAME]
    }
    
    suspend fun setSavedUsername(username: String?) {
        context.dataStore.edit { preferences ->
            if (username != null) {
                preferences[PreferencesKeys.SAVED_USERNAME] = username
            } else {
                preferences.remove(PreferencesKeys.SAVED_USERNAME)
            }
        }
    }
    
    // Session Management
    val sessionToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SESSION_TOKEN]
    }
    
    suspend fun setSessionToken(token: String?) {
        context.dataStore.edit { preferences ->
            if (token != null) {
                preferences[PreferencesKeys.SESSION_TOKEN] = token
            } else {
                preferences.remove(PreferencesKeys.SESSION_TOKEN)
            }
        }
    }
    
    val sessionExpiry: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SESSION_EXPIRY] ?: 0L
    }
    
    suspend fun setSessionExpiry(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SESSION_EXPIRY] = timestamp
        }
    }
    
    // Clear all preferences
    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
} 