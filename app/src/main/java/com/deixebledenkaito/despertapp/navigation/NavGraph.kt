package com.deixebledenkaito.despertapp.navigation


import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.CrearAlarmaScreen
import com.deixebledenkaito.despertapp.ui.screens.HomeScreen
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
import com.deixebledenkaito.despertapp.ui.screens.components.ButtonsBottomBar
import com.deixebledenkaito.despertapp.ui.screens.settings.SettingsScreen
import com.deixebledenkaito.despertapp.ui.screens.settings.components.AlarmSettingsScreen
import com.deixebledenkaito.despertapp.ui.screens.settings.components.SettingsTemes
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel


// 📦 com.despertapp.navigation

sealed class Screen(val route: String) {
    object AlarmList : Screen("alarm_list")
    object AlarmForm : Screen("alarm_form")
    object Settings : Screen("settings")
    object Temes : Screen("temes")
    object AlarmSettings : Screen("alarm_settings")
    object EditAlarm : Screen("edit_alarm/{alarmId}") {
        fun createRoute(alarmId: Int) = "edit_alarm/$alarmId"
    }
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
                    colors = BackgroundApp(currentThemeIsDark),
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
                    onNavigateToAlarmForm = { navController.navigate(Screen.AlarmForm.route) },
                    navController = navController
                )
            }
            composable(Screen.AlarmForm.route) {
                CrearAlarmaScreen(
                    onSave = { alarm ->
                        viewModel.addAlarm(alarm)
                        navController.popBackStack()
                    }

                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToAlarmSettings = { navController.navigate(Screen.AlarmSettings.route) },
                    onNavigateToTemes = { navController.navigate(Screen.Temes.route) }
                )


            }

            composable(Screen.AlarmSettings.route) {
                AlarmSettingsScreen()
            }
            composable(Screen.Temes.route) {
                SettingsTemes()
            }
            composable(
                route = Screen.EditAlarm.route,
                arguments = listOf(navArgument("alarmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val alarmId = backStackEntry.arguments?.getInt("alarmId") ?: return@composable
                val alarms by viewModel.alarms.collectAsStateWithLifecycle(initialValue = emptyList())

                val alarm = alarms.find { it.id == alarmId }

                if (alarm != null) {
                    CrearAlarmaScreen(
                        initialAlarm = alarm,
                        onSave = { updatedAlarm ->
                            viewModel.addAlarm(updatedAlarm.copy(id = alarmId))
                            navController.popBackStack()
                        }
                    )
                } else {
                    // No facis popBackStack immediatament!
                    // Esperem que potser encara no ha carregat
                    LaunchedEffect(alarms) {
                        if (alarms.isNotEmpty() && alarms.none { it.id == alarmId }) {
                            Log.d("NavGraph", "Alarm with ID $alarmId not found (després de carregar)")
                            navController.popBackStack()
                        }
                    }
                }
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
        Screen.AlarmSettings.route
    )
}
