package com.deixebledenkaito.despertapp.ui.screens.crearAlarma

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components.RodetaSeleccioHorizontal
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components.SegmentedControl
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectChallenge.SelectChallengeScreen
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds.SelectSoundScreen
import java.util.Calendar


@Composable
fun CrearAlarmaScreen(
    onSave: (AlarmEntity) -> Unit,
    modifier: Modifier = Modifier,
    initialAlarm: AlarmEntity? = null,
) {
    val calendar = remember { Calendar.getInstance() }


    var hour by remember {
        mutableIntStateOf(
            initialAlarm?.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
        )
    }
    var minute by remember {
        mutableIntStateOf(
            initialAlarm?.minute ?: calendar.get(Calendar.MINUTE)
        )
    }


    var selectedDays by remember { mutableStateOf(initialAlarm?.daysOfWeek ?: emptyList()) }

    var alarmName by remember { mutableStateOf(initialAlarm?.name ?: "") }

    // SO ALARMA
    var alarmSound by remember { mutableStateOf(initialAlarm?.alarmSound ?: "default") }
    var alarmSoundName by remember {
        mutableStateOf(
            initialAlarm?.alarmSoundName ?: "So per defecte"
        )
    }
    var showSoundSelector by remember { mutableStateOf(false) }

    // TIPUS DE REPTE
    var selectedChallenge by remember {
        mutableStateOf(
            initialAlarm?.challengeType ?: "Matemàtiques"
        )
    }
    var selectedChallengeName by remember {
        mutableStateOf(
            initialAlarm?.challengeType ?: "Matemàtiques"
        )
    }
    var showChallengeSelector by remember { mutableStateOf(false) }

    var repeatType by remember { mutableStateOf(initialAlarm?.repeticioDays ?: "Una vegada") }
    val repeatOptions = listOf("Personalitzat", "Una vegada", "Diàriament", "Dl a Dv")

    val days = listOf("Dl", "Dt", "Dc", "Dj", "Dv", "Ds", "Dg")
    var selectedModel by remember { mutableStateOf(initialAlarm?.testModel ?: "Bàsic") }
    val testModels = listOf("Bàsic", "Avançat", "Expert")

    var colorTextButtom = Color(0xF7676161)
    var fontSizeTitols = MaterialTheme.typography.titleSmall.copy(color = Color.White)
    val customSelectedDays = remember { mutableStateOf(listOf<Int>()) }


    if (initialAlarm != null) {
        customSelectedDays.value = selectedDays
    }

    // Actualitzem els dies seleccionats quan canvia el tipus de repetició
    LaunchedEffect(repeatType) {

        selectedDays = when (repeatType) {
            "Dl a Dv" -> (1..5).toList()
            "Diàriament" -> (1..7).toList()
            "Una vegada" -> emptyList()
            "Personalitzat" -> customSelectedDays.value
            else -> selectedDays
        }
    }

    if (showSoundSelector) {
        SelectSoundScreen(
            onSoundSelected = { soundId, soundName ->
                alarmSound = soundId
                alarmSoundName = soundName
                showSoundSelector = false
            },
            onCancel = { showSoundSelector = false }
        )
        return
    }

    if (showChallengeSelector) {
        SelectChallengeScreen(
            onChallengeSelected = { challengeId, challengeName ->
                selectedChallenge = challengeId
                selectedChallengeName = challengeName
                showChallengeSelector = false
            },
            onCancel = { showChallengeSelector = false },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = BackgroundApp(),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .padding(horizontal = 24.dp, vertical = 26.dp)
                .padding(bottom = 80.dp), // Espai per al botó fix
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Nom de l'alarma (opcional)
            OutlinedTextField(
                value = alarmName,
                onValueChange = { alarmName = it },
                label = {
                    Text(
                        "Nom de l'alarma (opcional)",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                },
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
                    .height(180.dp)
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
                        range = 0..23,
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
                        range = 0..59,
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

            // Afegim el nou selector de repetició
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Repetició",
                    style = fontSizeTitols
                )

                RodetaSeleccioHorizontal(
                    items = repeatOptions,
                    value = repeatType,
                    onValueChange = { repeatType = it }
                )
            }

            // Dies de la setmana amb xips animats
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Selecciona un dia",
                    style = fontSizeTitols
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    days.forEachIndexed { index, day ->
                        val dayNumber = index + 1
                        val selected = dayNumber in selectedDays
                        val enabled = when (repeatType) {
                            "Dl a Dv", "Diàriament" -> false
                            else -> true
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            AnimacioDiaChip(
                                day = day,
                                selected = selected,
                                onClick = {
                                    if (enabled) {
                                        selectedDays = if (selected) selectedDays - dayNumber
                                        else selectedDays + dayNumber
                                    }
                                    customSelectedDays.value = selectedDays
                                },
                                enabled = enabled
                            )
                        }
                    }
                }
            }

            // Tipus de repte (nova implementació)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tipus de repte",
                    style = fontSizeTitols
                )

                OutlinedButton(
                    onClick = { showChallengeSelector = true },
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
                            text = selectedChallengeName,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Seleccionar repte",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Model de prova amb selector modern
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Nivell",
                    style = fontSizeTitols
                )

                SegmentedControl(
                    items = testModels,
                    selectedItem = selectedModel,
                    onItemSelect = { selectedModel = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Nova secció per seleccionar el so
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "So de l'alarma",
                    style = fontSizeTitols
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
        }

        // Botó de guardar fix a la part inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)

        ) {
            Button(
                onClick = {
                    val newAlarm = AlarmEntity(
                        hour = hour,
                        minute = minute,
                        daysOfWeek = when (repeatType) {
                            "Una vegada" -> selectedDays
                            "Dl a Dv" -> listOf(1, 2, 3, 4, 5) // Dl a Dv
                            "Diàriament" -> (1..7).toList() // Tots els dies "Diariament""
                            else -> selectedDays // Selecció manual per "Diàriament"
                        },
                        isActive = true,
                        testModel = selectedModel,
                        name = alarmName,
                        alarmSound = alarmSound,
                        alarmSoundName = alarmSoundName,
                        challengeType = selectedChallenge,
                        isRecurring = repeatType != "Una vegada",
                        repeticioDays = repeatType
                    )
                    onSave(newAlarm)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp)
                    .height(56.dp),

                enabled = selectedDays.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color(0xFF282727)  // Blanc amb ~10% alpha
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "GUARDAR ALARMA",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (selectedDays.isNotEmpty()) Color.Black else colorTextButtom,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}