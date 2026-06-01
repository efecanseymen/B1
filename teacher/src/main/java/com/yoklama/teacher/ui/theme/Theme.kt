package com.yoklama.teacher.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7C9EFF),
    onPrimary = Color(0xFF001B73),
    primaryContainer = Color(0xFF1A2F8A),
    onPrimaryContainer = Color(0xFFDAE2FF),
    secondary = Color(0xFFBEC6DC),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2A2A2A),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6),
    error = Color(0xFFCF6679)
)

@Composable
fun TeacherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
