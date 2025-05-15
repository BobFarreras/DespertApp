@file:Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")

package com.deixebledenkaito.despertapp.presentation.components.items

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate



@Composable
fun AlarmItem(
    alarmId :String,
    time: String,
    label: String,
    days: List<String>,
    enabled: Boolean,
    onToggleAlarm: (String, Boolean) -> Unit
) {
    // Estat local per a controlar el switch, inicialitzat amb enabled
    val isSwitched = enabled
    val today = currentDayShortName()
    val goldColor = Color(0xEDC9B62D)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { /* TODO: Navegar o obrir diàleg edició */ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSwitched) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Columna amb hora, etiqueta i dies actius
            Column {
                Text(
                    text = time,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    days.forEach { day ->
                        val isToday = day == today
                        val backgroundColor = when {
                            isToday -> goldColor
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        }
                        val textColor = when {
                            isToday -> Color.Black
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(color = backgroundColor, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor
                            )
                        }
                    }
                }
            }
            // Switch per activar o desactivar l'alarma
            Switch(
                checked = isSwitched,
                onCheckedChange = {onToggleAlarm(alarmId, it) }, // Notifica el canvi al ViewModel o similar
                modifier = Modifier.semantics {
                    contentDescription = if (isSwitched) "Alarma activada" else "Alarma desactivada"
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}


fun currentDayShortName(): String {
    return when (LocalDate.now().dayOfWeek) {
        DayOfWeek.MONDAY -> "Dl"
        DayOfWeek.TUESDAY -> "Dt"
        DayOfWeek.WEDNESDAY -> "Dc"
        DayOfWeek.THURSDAY -> "Dj"
        DayOfWeek.FRIDAY -> "Dv"
        DayOfWeek.SATURDAY -> "Ds"
        DayOfWeek.SUNDAY -> "Dg"
    }
}
