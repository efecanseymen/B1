package com.efecanseymen.b1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.efecanseymen.b1.ui.screens.MainScreen
import com.efecanseymen.b1.ui.screens.Navigation
import com.efecanseymen.b1.ui.theme.B1Theme
import com.efecanseymen.b1.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color(0xFF121212).toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color(0xFF121212).toArgb())
        )
        setContent {
            B1Theme {
                Navigation(viewModel = viewModel)
            }
        }
    }
}


