package com.deixebledenkaito.despertapp.navigation


import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.CrearAlarmaScreen
import com.deixebledenkaito.despertapp.ui.screens.HomeScreen
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
import com.deixebledenkaito.despertapp.ui.screens.components.ButtonsBottomBar
import com.deixebledenkaito.despertapp.ui.screens.settings.SettingsScreen
import com.deixebledenkaito.despertapp.ui.screens.settings.components.AlarmSettingsScreen
import com.deixebledenkaito.despertapp.ui.screens.settings.components.LanguageSettingsScreen
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel


// 📦 com.despertapp.navigation

sealed class Screen(val route: String) {
    object AlarmList : Screen("alarm_list")
    object AlarmForm : Screen("alarm_form")
    object Settings : Screen("settings")
    object LanguageSettings : Screen("language")
    object AlarmSettings : Screen("alarm_settings")
}

@Composable
fun NavGraph(
    viewModel: AlarmViewModel,
    startDestination: String = Screen.AlarmList.route
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = BackgroundApp(),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Screen.AlarmList.route) {
                val context = LocalContext.current
                val activity = context as? Activity

                // 🔙 Captura el back press i surt de l'app
                BackHandler {
                    activity?.finish()
                }

                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAlarmForm = { navController.navigate(Screen.AlarmForm.route) }
                )
            }
            composable(Screen.AlarmForm.route) {
                CrearAlarmaScreen(
                    onAdd = { alarm ->
                        viewModel.addAlarm(alarm)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToLanguageSettings = { navController.navigate(Screen.LanguageSettings.route) },
                    onNavigateToAlarmSettings = { navController.navigate(Screen.AlarmSettings.route) },
                    onLogout = { /* Manejar logout */ }
                )
            }
            composable(Screen.LanguageSettings.route) {
                LanguageSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AlarmSettings.route) {
                AlarmSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
        // 🔽 Afegim el BottomBar només a les rutes que ens interessen
        if (shouldShowBottomBar(currentRoute)) {
            ButtonsBottomBar(
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onAlarmClick = { navController.navigate(Screen.AlarmList.route) },
                selectedButtom = when (currentRoute) {
                    Screen.AlarmList.route -> "Alarmes"
                    Screen.Settings.route,
                    Screen.LanguageSettings.route,
                    Screen.AlarmSettings.route -> "Settings"
                    else -> ""
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)

            )
        }
    }

}


fun shouldShowBottomBar(route: String?): Boolean {
    return route in listOf(
        Screen.AlarmList.route,
        Screen.Settings.route,
        Screen.LanguageSettings.route,
        Screen.AlarmSettings.route
    )
}
