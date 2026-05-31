package com.efecanseymen.b1.ui.screens

// import ClassScreen  ← BU SATIRI SİL

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.efecanseymen.b1.viewmodel.HomeViewModel

@Composable
fun MainScreen(viewModel: HomeViewModel, onLogOutClick: () -> Unit){
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        Triple("Ana Sayfa", Icons.Filled.Home, 0),
        Triple("Yoklama", Icons.Filled.AssignmentTurnedIn, 1),
        Triple("Hangi Derslik", Icons.Filled.Nfc, 2)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, color = MaterialTheme.colorScheme.onSurface) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeScreen(
                viewModel = viewModel,
                onLogOutClick = onLogOutClick,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> ClassScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> ScanScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}