package com.deixebledenkaito.despertapp.navigation


import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.ui.screens.AlarmFormScreen
import com.deixebledenkaito.despertapp.ui.screens.AlarmListScreen


// 📦 com.despertapp.navigation

sealed class Screen(val route: String) {
    object AlarmList : Screen("alarm_list")
    object AlarmForm : Screen("alarm_form")
}

@Composable
fun NavGraph(
    alarms: List<AlarmEntity>,
    onDelete: (AlarmEntity) -> Unit,
    onAdd: (AlarmEntity) -> Unit,
    startDestination: String = Screen.AlarmList.route
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.AlarmList.route) {
            AlarmListScreen(
                alarms = alarms,
                onDelete = onDelete,
                onAddClick = { navController.navigate(Screen.AlarmForm.route) }
            )
        }
        composable(Screen.AlarmForm.route) {
            AlarmFormScreen(onAdd = {
                onAdd(it)
                navController.popBackStack() // Torna enrere després de guardar
            })
        }
    }
}