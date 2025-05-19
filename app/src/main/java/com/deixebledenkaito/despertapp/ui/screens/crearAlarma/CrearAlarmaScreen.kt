package com.deixebledenkaito.despertapp.ui.screens.crearAlarma

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField


import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp


import com.deixebledenkaito.despertapp.data.AlarmEntity


import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components.AnimacioDiaChip
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components.RodaTempsPicker
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components.SegmentedControl
import com.deixebledenkaito.despertapp.ui.screens.selectsounds.SelectSoundScreen


@Composable
fun CrearAlarmaScreen(
    onAdd: (AlarmEntity) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hour by remember { mutableIntStateOf(12) }
    var minute by remember { mutableIntStateOf(30) }
    var selectedDays by remember { mutableStateOf(listOf<Int>()) }
    var selectedModel by remember { mutableStateOf("Bàsic") }
    var alarmName by remember { mutableStateOf("") }

//    SO ALARMA
    var alarmSound by remember { mutableStateOf("default") }
    var alarmSoundName by remember { mutableStateOf("So per defecte") }
    var showSoundSelector by remember { mutableStateOf(false) }


    val days = listOf("Dl", "Dt", "Dc", "Dj", "Dv", "Ds", "Dg")
    val testModels = listOf("Bàsic", "Avançat", "Expert")

    var selectedChallenge by remember { mutableStateOf("Matemàtiques") }
    val challengeTypes = listOf("Matemàtiques", "Cultura Catalana")

    var colorTextButtom = Color(0xF7676161)

    if (showSoundSelector) {
        SelectSoundScreen(
            onSoundSelected = { soundId, soundName ->
                alarmSound = soundId
                alarmSoundName = soundName
                showSoundSelector = false
            },
            onCancel = { showSoundSelector = false },
            modifier = Modifier.fillMaxSize()
        )
        return
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = BackgroundApp(),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Capçalera amb títol i botó de tancar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tancar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Nom de l'alarma (opcional)
        OutlinedTextField(
            value = alarmName,
            onValueChange = { alarmName = it },
            label = { Text("Nom de l'alarma (opcional)", color = Color.White.copy(alpha = 0.8f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White.copy(alpha = 0.8f),
                unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Time Picker amb rodets (Wheel Style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector d'hores
                RodaTempsPicker(
                    value = hour,
                    onValueChange = { hour = it },
                    range = 0..24,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = ":",
                    style = MaterialTheme.typography.displaySmall.copy(color = Color.White),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Selector de minuts
                RodaTempsPicker(
                    value = minute,
                    onValueChange = { minute = it },
                    range = 0..60,
                    modifier = Modifier.weight(1f)
                )

                // AM/PM (opcional per a format 12h)
                Text(
                    text = if (hour < 12) "AM" else "PM",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        // Dies de la setmana amb xips animats
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Selecciona un dia",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                days.forEachIndexed { index, day ->
                    val selected = (index + 1) in selectedDays
                    Box(modifier = Modifier.weight(1f)) {
                        AnimacioDiaChip(
                            day = day,
                            selected = selected,
                            onClick = {
                                selectedDays = if (selected) selectedDays - (index + 1)
                                else selectedDays + (index + 1)
                            }
                        )
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Tipus de repte",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )

            SegmentedControl(
                items = challengeTypes,
                selectedItem = selectedChallenge,
                onItemSelect = { selectedChallenge = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Model de prova amb selector modern
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Model de prova",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )

            SegmentedControl(
                items = testModels,
                selectedItem = selectedModel,
                onItemSelect = { selectedModel = it },
                modifier = Modifier.fillMaxWidth()
            )
        }


        if (selectedDays.isNotEmpty()){
            colorTextButtom = Color.Black

        }
        // Nova secció per seleccionar el so
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "So de l'alarma",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )

            OutlinedButton(
                onClick = { showSoundSelector = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alarmSoundName,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Seleccionar so",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        // Botó de guardar amb efecte
        Button(
            onClick = {
                val newAlarm = AlarmEntity(
                    hour = hour,
                    minute = minute,
                    daysOfWeek = selectedDays,
                    isActive = true,
                    testModel = selectedModel,
                    name = alarmName, // Afegeix el nom aquí
                    alarmSound = alarmSound,
                    alarmSoundName = alarmSoundName,
                    challengeType = selectedChallenge
                )
                onAdd(newAlarm)


            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedDays.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.9f),
                disabledContainerColor = Color.White.copy(alpha = 0.1f)

            ),
            shape = RoundedCornerShape(12.dp)
        ) {

            Text(

                text = "GUARDAR ALARMA",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = colorTextButtom,

                    fontWeight = FontWeight.Bold
                ),


            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}




