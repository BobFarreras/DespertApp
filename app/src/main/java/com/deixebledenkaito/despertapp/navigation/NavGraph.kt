package com.deixebledenkaito.despertapp.navigation


import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.CrearAlarmaScreen
import com.deixebledenkaito.despertapp.ui.screens.HomeScreen
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

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.AlarmList.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAlarmForm = { navController.navigate(Screen.AlarmForm.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToListAlarm = { navController.navigate(Screen.AlarmList.route) }
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
                onBack = { navController.popBackStack() },
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
}