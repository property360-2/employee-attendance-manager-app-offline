package com.malikhain.employee_attendance_manager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ModernLightColorScheme = lightColorScheme(
    primary = ModernPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = ModernPrimaryContainer,
    onPrimaryContainer = ModernOnPrimaryContainer,
    secondary = ModernSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = ModernSecondaryContainer,
    onSecondaryContainer = ModernOnSecondaryContainer,
    tertiary = ModernTertiary,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = ModernTertiaryContainer,
    onTertiaryContainer = ModernOnTertiaryContainer,
    error = ModernError,
    onError = Color(0xFFFFFFFF),
    errorContainer = ModernErrorContainer,
    onErrorContainer = Color(0xFF7F1D1D),
    background = ModernBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = ModernSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = ModernSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = ModernOutlineLight,
    outlineVariant = ModernNeutral200,
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF0F172A),
    inverseOnSurface = Color(0xFFF8FAFC),
    inversePrimary = ModernPrimaryLight,
    surfaceDim = ModernNeutral100,
    surfaceBright = ModernSurfaceLight,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = ModernNeutral50,
    surfaceContainer = ModernNeutral100,
    surfaceContainerHigh = ModernNeutral200,
    surfaceContainerHighest = ModernNeutral300,
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = ModernPrimaryLight,
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = ModernPrimaryDark,
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = ModernSecondaryLight,
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = ModernSecondaryDark,
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = ModernTertiaryLight,
    onTertiary = Color(0xFF78350F),
    tertiaryContainer = ModernTertiaryDark,
    onTertiaryContainer = Color(0xFFFEF3C7),
    error = ModernErrorLight,
    onError = Color(0xFF7F1D1D),
    errorContainer = ModernErrorDark,
    onErrorContainer = Color(0xFFFEE2E2),
    background = ModernBackgroundDark,
    onBackground = Color(0xFFF8FAFC),
    surface = ModernSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = ModernSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = ModernOutlineDark,
    outlineVariant = ModernNeutral700,
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF0F172A),
    inversePrimary = ModernPrimaryDark,
    surfaceDim = ModernNeutral900,
    surfaceBright = ModernNeutral800,
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = ModernNeutral900,
    surfaceContainer = ModernNeutral800,
    surfaceContainerHigh = ModernNeutral700,
    surfaceContainerHighest = ModernNeutral600,
)

@Composable
fun EmployeeattendancemanagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to use our custom modern palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ModernDarkColorScheme
        else -> ModernLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use surface color for status bar to match Material 3 guidelines
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}