package com.yoklama.teacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.yoklama.teacher.ui.screens.Navigation
import com.yoklama.teacher.ui.theme.TeacherTheme
import com.yoklama.teacher.viewmodel.TeacherViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TeacherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color(0xFF121212).toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color(0xFF121212).toArgb())
        )
        setContent {
            TeacherTheme {
                Navigation(viewModel = viewModel)
            }
        }
    }
}
