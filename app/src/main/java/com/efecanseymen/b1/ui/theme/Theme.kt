package com.efecanseymen.b1.ui.theme

import android.R
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Koyu tema renkleri
private val MidnightVioletColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),        // Pastel Mor
    onPrimary = Color(0xFF3700B3),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1F1B24),        // Hafif morumsu koyu yüzey
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF332940),
    secondary = Color(0xFF03DAC6),      // Teal (Tamamlayıcı renk)
    error = Color(0xFFCF6679)
)

@Composable
fun B1Theme(content: @Composable () -> Unit) {
    val context = LocalContext.current

    val colorScheme = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
        dynamicDarkColorScheme(context)
    } else {
        MidnightVioletColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}