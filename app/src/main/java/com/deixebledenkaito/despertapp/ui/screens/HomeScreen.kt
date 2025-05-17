package com.deixebledenkaito.despertapp.ui.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp

import com.deixebledenkaito.despertapp.ui.screens.components.AddAlarmFAB
import com.deixebledenkaito.despertapp.ui.screens.components.AlarmListContent


import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel



@Composable
fun HomeScreen(
    viewModel: AlarmViewModel,
    onLogout: () -> Unit,
    onNavigateToAlarmForm: () -> Unit
) {

    val alarms by viewModel.alarms.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Log.d("HomeScreen", "Renderitzant pantalla principal")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = BackgroundApp(),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),

        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        AlarmListContent(alarms = alarms, viewModel = viewModel, onNavigateToAlarmForm = onNavigateToAlarmForm)
        AddAlarmFAB(onClick = onNavigateToAlarmForm)
    }


}





