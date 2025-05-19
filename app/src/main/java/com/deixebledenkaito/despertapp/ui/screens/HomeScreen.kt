package com.deixebledenkaito.despertapp.ui.screens


import android.util.Log
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth


import androidx.compose.foundation.layout.padding


import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp

import com.deixebledenkaito.despertapp.ui.screens.components.AddAlarmFAB
import com.deixebledenkaito.despertapp.ui.screens.components.AlarmListContent
import com.deixebledenkaito.despertapp.ui.screens.components.ButtonsBottomBar


import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel



@Composable
fun HomeScreen(
    viewModel: AlarmViewModel,
    onNavigateToAlarmForm: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToListAlarm: () -> Unit
) {

    val alarms by viewModel.alarms.collectAsState()


    Log.d("HomeScreen", "Renderitzant pantalla principal")
    Box(modifier = Modifier.fillMaxSize()) {
        // Fons de pantalla (gradient)
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
        // Columna principal que conté la llista i els botons inferiors
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Llista d'alarmes (ocuparà tot l'espai disponible menys l'ocupat pels botons)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AlarmListContent(
                    alarms = alarms,
                    viewModel = viewModel,
                    onNavigateToAlarmForm = onNavigateToAlarmForm
                )
            }

            // Barra inferior de botons
            ButtonsBottomBar(
                onSettingsClick = onNavigateToSettings,
                onAlarmClick = onNavigateToListAlarm,
                modifier = Modifier.fillMaxWidth(),
                selectedButtom = "Alarmes",
            )
        }

        // Floating Action Button (posicionat absolutament sobre els altres elements)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp) // Ajusta aquest padding segons la mida del teu ButtonsBottomBar
        ) {
            AddAlarmFAB(onClick = onNavigateToAlarmForm)
        }
    }
}