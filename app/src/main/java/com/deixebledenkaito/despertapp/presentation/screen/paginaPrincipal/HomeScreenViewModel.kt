package com.deixebledenkaito.despertapp.presentation.screen.paginaPrincipal

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.deixebledenkaito.despertapp.domain.model.formState.HomeScreenState
import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository

import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.GetAlarmsUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.ToggleAlarmUseCase
import com.deixebledenkaito.despertapp.presentation.broadcast.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val toggleAlarmUseCase: ToggleAlarmUseCase,
    private val alarmScheduler: AlarmScheduler // Afegim el programador de l'alarma
) : ViewModel() {

    private val _uiState = mutableStateOf(HomeScreenState())
    val uiState: State<HomeScreenState> = _uiState

    init {
        Log.d("HomeScreenVM", "Inicializando ViewModel")
        loadUserData()
        loadAlarms()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            Log.d("HomeScreenVM", "Cargant dades de l'usuari...")
            authRepository.getCurrentUser()?.let { user ->
                _uiState.value = _uiState.value.copy(
                    userName = user.displayName ?: "Usuari",
                    profileImageUrl = user.photoUrl?.toString()
                )
                Log.d("HomeScreenVM", "Usuario cargado: ${user.displayName}")
            }
        }
    }

    // A HomeScreenViewModel
    private fun loadAlarms() {
        Log.d("HomeScreenVM", "Cargant alarmas...")
        viewModelScope.launch {
            getAlarmsUseCase().collect { alarms ->
                val sortedAlarms = alarms.sortedBy { it.hour * 100 + it.minute }
                _uiState.value = _uiState.value.copy(alarms = sortedAlarms)

                // Programar alarmas activas
                sortedAlarms.filter { it.enabled }.forEach { alarm ->
                    Log.d("HomeScreenVM", "Programant alarmes: ${alarm.id} a las ${alarm.getFormattedTime()}")
                    alarmScheduler.scheduleAlarm(alarm)
                }
                Log.d("HomeScreenVM", "Alarmes cargades: ${sortedAlarms.size}")
            }
        }
    }

    fun toggleAlarm(enabled: Boolean, alarmId: String) {
        viewModelScope.launch {
            Log.d("HomeScreenVM", "Cambiando estado de alarma $alarmId a $enabled")
            toggleAlarmUseCase(alarmId, enabled)

            // Actualizar programación de alarma
            uiState.value.alarms.find { it.id == alarmId }?.let { alarm ->
                if (enabled) {
                    alarmScheduler.scheduleAlarm(alarm.copy(enabled = true))
                    Log.d("HomeScreenVM", "Alarma $alarmId programada")
                } else {
                    alarmScheduler.cancelAlarm(alarmId)
                    Log.d("HomeScreenVM", "Alarma $alarmId cancelada")
                }
            }
        }
    }


}