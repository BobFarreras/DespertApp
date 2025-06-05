package com.deixebledenkaito.despertapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.navigation.Screen
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark

import kotlinx.coroutines.delay


@Composable
fun AlarmItemCard(
    alarm: AlarmEntity,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController // <- AFEGIT
) {
    var showMenu by remember { mutableStateOf(false) }
    val textColor = if (currentThemeIsDark) Color.White else Color.Black

    var tempsActualitzat by remember { mutableStateOf(calcularTempsRestant(alarm)) }

    LaunchedEffect(alarm) {
        while (true) {
            delay(60 * 1000) // Cada minut
            tempsActualitzat = calcularTempsRestant(alarm)

        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = textColor.copy(alpha = 0.1f),
            contentColor = textColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Primera fila: Hora y switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "%02d:%02d".format(alarm.hour, alarm.minute),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.W600,
                            color = textColor
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (alarm.name.length <= 20) alarm.name else "${alarm.name.take(20)}...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = textColor.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )

                }

                Switch(
                    checked = alarm.isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = textColor,
                        checkedTrackColor = textColor.copy(alpha = 0.3f),
                        uncheckedThumbColor = textColor.copy(alpha = 0.4f),
                        uncheckedTrackColor = textColor.copy(alpha = 0.1f)
                    )
                )
            }

            // Días de la semana
            WeekDaysRow(
                selectedDays = alarm.daysOfWeek,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Modelo de prueba
                Text(
                    text = "Falten: ",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = textColor.copy(alpha = 0.5f)
                    )
                )
                Text(
                    text = formatarDuracio(tempsActualitzat),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = textColor.copy(alpha = 0.85f)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // Menú de opciones (tres puntos)
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Opciones",
                            tint = textColor.copy(alpha = 0.8f)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.75f)),


                    ) {

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Modificar",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Green.copy(alpha = 0.45f)
                                    )
                                )
                            },
                            onClick = {
                                showMenu = false
                                navController.navigate(Screen.EditAlarm.createRoute(alarm.id))
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Mode,
                                    contentDescription = null,
                                    tint = Color.Green.copy(alpha = 0.45f)
                                )
                            }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.35f), modifier =  Modifier.padding(horizontal = 12.dp))
                        DropdownMenuItem(
                            text = {
                                // tipus repeticio dia
                                Text(
                                    text = alarm.repeticioDays,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                )
                            },
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Replay,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                // So de l'alarma
                                Text(
                                    text = alarm.challengeType,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                )
                            },
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                // Modelo de prueba
                                Text(

                                    text = alarm.testModel,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                )
                            },
                            onClick = {
                                showMenu = false
                                navController.navigate(Screen.EditAlarm.createRoute(alarm.id))
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Leaderboard,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                // So de l'alarma
                                Text(
                                    text = alarm.alarmSoundName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                )
                            },
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.35f), modifier =  Modifier.padding(horizontal = 12.dp))
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Eliminar",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekDaysRow(
    selectedDays: List<Int>,
    modifier: Modifier = Modifier
) {
    val days = listOf("Dl", "Dt", "Dc", "Dj", "Dv", "Ds", "Dg")
    val textColor = if (currentThemeIsDark) Color.White else Color.Black
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        days.forEachIndexed { index, day ->
            val dayNumber = index + 1
            val isSelected = selectedDays.contains(dayNumber)

            Text(
                text = day,
                color = if (isSelected) {
                    if(currentThemeIsDark) MaterialTheme.colorScheme.primary else textColor
                } else {
                    textColor.copy(alpha = 0.4f)
                },
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
