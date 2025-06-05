package com.deixebledenkaito.despertapp.ui.screens
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
import com.deixebledenkaito.despertapp.ui.screens.components.AddAlarmFAB
import com.deixebledenkaito.despertapp.ui.screens.components.AlarmListContent
import com.deixebledenkaito.despertapp.ui.screens.components.EmptyAlarmsState
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel

@Composable
fun HomeScreen(
    viewModel: AlarmViewModel,
    onNavigateToAlarmForm: () -> Unit,
    navController: NavController,
) {

    val alarms by viewModel.alarms.collectAsState(initial = null)
    val textColor = if (currentThemeIsDark) Color.White else Color.Black

    Log.d("HomeScreen", "Renderitzant pantalla principal")
    Box(modifier = Modifier.fillMaxSize()) {
        // Fons de pantalla (gradient)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = BackgroundApp(currentThemeIsDark),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        // Columna principal que conté la llista i els botons inferiors
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Llista d'alarmes (ocuparà tot l'espai disponible menys l'ocupat pels botons)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when{
                    alarms == null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = textColor)
                        }
                    }
                    alarms!!.isEmpty() -> {
                        Log.d("AlarmListContent", "Mostrant estat buit")
                        EmptyAlarmsState(onNavigateToAlarmForm)
                    }
                    else -> {
                        AlarmListContent(
                            alarms = alarms!!,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }


            }


        }

        // Floating Action Button (posicionat absolutament sobre els altres elements)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp) // Ajusta aquest padding segons la mida del teu ButtonsBottomBar
        ) {
            AddAlarmFAB(onClick = onNavigateToAlarmForm)
        }
    }

}