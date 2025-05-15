package com.deixebledenkaito.despertapp.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField


import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.deixebledenkaito.despertapp.data.AlarmEntity

@Composable
fun AlarmFormScreen(onAdd: (AlarmEntity) -> Unit) {
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(listOf<Int>()) }

    val days = listOf("Dl", "Dt", "Dc", "Dj", "Dv", "Ds", "Dg")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Nova Alarma", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = hour,
            onValueChange = { hour = it.filter { c -> c.isDigit() } },
            label = { Text("Hora") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = minute,
            onValueChange = { minute = it.filter { c -> c.isDigit() } },
            label = { Text("Minut") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Dies actius:", fontWeight = FontWeight.SemiBold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            days.forEachIndexed { index, day ->
                val selected = (index + 1) in selectedDays
                FilterChip(
                    selected = selected,
                    onClick = {
                        selectedDays = if (selected) selectedDays - (index + 1)
                        else selectedDays + (index + 1)
                    },
                    label = { Text(day) }
                )
            }
        }

        Button(
            onClick = {
                val alarm = AlarmEntity(
                    hour = hour.toIntOrNull() ?: 0,
                    minute = minute.toIntOrNull() ?: 0,
                    daysOfWeek = selectedDays,
                    isActive = true
                )
                onAdd(alarm)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Alarma")
        }
    }
}

