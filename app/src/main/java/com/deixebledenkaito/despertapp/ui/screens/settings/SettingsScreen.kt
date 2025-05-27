package com.deixebledenkaito.despertapp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm

import androidx.compose.material.icons.filled.Language


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp

import com.deixebledenkaito.despertapp.ui.screens.settings.components.SettingsCard

@Composable
fun SettingsScreen(
    onNavigateToLanguageSettings: () -> Unit,
    onNavigateToAlarmSettings: () -> Unit,


) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fons de pantalla (gradient) - igual que a HomeScreen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = BackgroundApp(),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Títol
            Text(
                text = "Configuració",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Llista d'opcions de configuració
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
//                item {
//                    SettingsCard(
//                        title = "Configuració general",
//                        icon = Icons.Default.Settings,
//                        onClick = { /* Navigate to general settings */ }
//                    )
//                }
                item {
                    SettingsCard(
                        title = "Configuració d'alarmes",
                        icon = Icons.Default.Alarm,
                        onClick = onNavigateToAlarmSettings
                    )
                }
                item {
                    SettingsCard(
                        title = "Idioma",
                        icon = Icons.Default.Language,
                        onClick = onNavigateToLanguageSettings
                    )
                }

//                item {
//                    SettingsCard(
//                        title = "Tema",
//                        icon = Icons.Default.ColorLens,
//                        onClick = { /* Navigate to theme settings */ }
//                    )
//                }
//                item {
//                    SettingsCard(
//                        title = "Tancar sessió",
//                        icon = Icons.Default.ExitToApp,
//                        onClick = onLogout,
//                        isDestructive = true
//                    )
//                }
            }

        }



    }

}