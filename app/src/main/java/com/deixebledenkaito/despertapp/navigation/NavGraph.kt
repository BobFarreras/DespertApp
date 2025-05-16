package com.deixebledenkaito.despertapp.navigation


import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.CrearAlarmaScreen
import com.deixebledenkaito.despertapp.ui.screens.HomeScreen
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel


// 📦 com.despertapp.navigation

sealed class Screen(val route: String) {
    object AlarmList : Screen("alarm_list")
    object AlarmForm : Screen("alarm_form")
}

@Composable
fun NavGraph(
    viewModel: AlarmViewModel,
    onLogout: () -> Unit,
    startDestination: String = Screen.AlarmList.route
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.AlarmList.route) {
            HomeScreen(
                viewModel = viewModel,
                onLogout = onLogout,
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
    }
}