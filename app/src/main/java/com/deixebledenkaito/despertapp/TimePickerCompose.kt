package com.deixebledenkaito.despertapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun TimePickerCompose(calendar: Calendar, onCalendarChanged: (Calendar) -> Unit) {
    var hour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberPicker("Hora", hour, 0, 23) {
            hour = it
            val updated = calendar.clone() as Calendar
            updated.set(Calendar.HOUR_OF_DAY, hour)
            onCalendarChanged(updated)
        }
        Spacer(modifier = Modifier.width(16.dp))
        NumberPicker("Minut", minute, 0, 59) {
            minute = it
            val updated = calendar.clone() as Calendar
            updated.set(Calendar.MINUTE, minute)
            onCalendarChanged(updated)
        }
    }
}

@Composable
fun NumberPicker(label: String, value: Int, min: Int, max: Int, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = min.toFloat()..max.toFloat()
        )
        Text("$value")
    }
}
