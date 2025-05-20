package com.deixebledenkaito.despertapp.ui.screens.components

import android.util.Log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.data.AlarmEntity

import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel


@Composable
fun AlarmListContent(
    alarms: List<AlarmEntity>,
    viewModel: AlarmViewModel,
    modifier: Modifier = Modifier,
    onNavigateToAlarmForm: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()

    // Estat local per a la llista d'alarmes
    LaunchedEffect(alarms) {
        Log.d("AlarmListContent", "Rebuda llista d'alarmes amb ${alarms.size} elements")
    }
    Log.d("AlarmListContent", "isLoading $isLoading")

    if(isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    }
    else{
        if(alarms.isEmpty()) {
            Log.d("AlarmListContent", "Mostrant estat buit")
            EmptyAlarmsState(modifier, onNavigateToAlarmForm)
        } else {

            LazyColumn(
                modifier = modifier
                    .padding(top = 30.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(alarms, key = { it.id }) { alarm ->
                    Log.d("AlarmListContent", "Renderitzant alarma ID: ${alarm.id}")
                    AlarmItemCard(
                        alarm = alarm,
                        onToggleActive = { isActive ->
                            Log.d("AlarmListContent", "Canviant estat alarma ${alarm.id}")
                            viewModel.toggleAlarmActive(alarm, isActive)
                        },
                        onDelete = {
                            Log.d("AlarmListContent", "Eliminant alarma ${alarm.id}")
                            viewModel.deleteAlarm(alarm)
                        }
                    )
                }
            }
         }
    }
}