package com.deixebledenkaito.despertapp.ui.screens.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTopBar(
    showMenu: Boolean,
    onMenuToggle: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "DespertApp",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            TopBarDesplegable(
                showMenu = showMenu,
                onMenuToggle = onMenuToggle,

            )
        }
    )
}