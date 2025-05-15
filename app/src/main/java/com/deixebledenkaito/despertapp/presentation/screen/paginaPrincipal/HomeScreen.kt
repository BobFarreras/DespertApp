
package com.deixebledenkaito.despertapp.presentation.screen.paginaPrincipal


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding


import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button

import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch

import androidx.compose.material3.Text

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import com.deixebledenkaito.despertapp.domain.model.Alarm
import com.deixebledenkaito.despertapp.presentation.components.crearAlarma.AlarmCreationCard
import com.deixebledenkaito.despertapp.presentation.components.items.AlarmItem
import com.deixebledenkaito.despertapp.presentation.components.topBar.TolBarDesplegable

import java.time.DayOfWeek
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onLogout: () -> Unit,

    ) {

    val uiState by viewModel.uiState
    var showMenu by remember { mutableStateOf(false) }
    var isCreatingAlarm by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hola, ${uiState.userName}!",
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
                    TolBarDesplegable (
                        showMenu = showMenu,
                        onMenuToggle = { showMenu = it },
                        onLogout = onLogout
                    )
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            else -> AlarmContent(
                paddingValues = paddingValues,
                alarms = uiState.alarms,
                isCreatingAlarm = isCreatingAlarm,
                onStartCreateAlarm = { isCreatingAlarm = true },
                onCancelCreateAlarm = { isCreatingAlarm = false },
                onToggleAlarm = { alarmId, enabled ->
                    viewModel.toggleAlarm(enabled, alarmId)
                },
                userId = uiState.userName
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AlarmContent(
    paddingValues: PaddingValues,
    alarms: List<Alarm>,
    isCreatingAlarm: Boolean,
    onStartCreateAlarm: () -> Unit,
    onCancelCreateAlarm: () -> Unit,
    onToggleAlarm: (String, Boolean) -> Unit,
    userId:String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                    )
                )
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isCreatingAlarm,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AlarmCreationCard(
                viewModel = hiltViewModel(), // o passat des del `HomeScreenViewModel`
                userId = userId,
                onCancel = onCancelCreateAlarm
            )
        }

        if (!isCreatingAlarm) {
            CreateAlarmButton(onClick = onStartCreateAlarm)
        }

        AlarmList(
            alarms = alarms,
            onToggleAlarm = onToggleAlarm
        )

    }
}





@Composable
fun CreateAlarmButton(onClick: () -> Unit) {
    Button(
        onClick = {
            Log.d("CreateAlarmButton", "Botó premut per crear nova alarma")
            onClick()
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text("Afegir Alarma", style = MaterialTheme.typography.labelLarge)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmList(
    alarms: List<Alarm>,
    onToggleAlarm: (String, Boolean) -> Unit
) {
    Column {
        alarms.forEach { alarm ->
            key(alarm.id) {
                Spacer(modifier = Modifier.height(16.dp))
                AlarmItem(
                    alarmId = alarm.id,
                    time = alarm.getFormattedTime(),
                    label = alarm.label,
                    days = alarm.getDayNames(),
                    enabled = alarm.enabled,
                    onToggleAlarm = onToggleAlarm
                )
            }
        }
    }
}






