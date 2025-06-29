package com.malikhain.employee_attendance_manager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.malikhain.employee_attendance_manager.utils.NotificationUtils

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        NotificationUtils.createNotificationChannel(this)
    }
}
