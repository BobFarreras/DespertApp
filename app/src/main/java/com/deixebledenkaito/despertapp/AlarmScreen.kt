package com.deixebledenkaito.despertapp

// AlarmScreen.kt
import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AlarmScreen() {
    val context = LocalContext.current
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE1BEE7))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⏰ DespertApp", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TimePickerCompose(calendar) { updatedCalendar ->
            calendar = updatedCalendar
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            setAlarm(context, calendar)
        }) {
            Text("Activar alarma")
        }
    }
}
