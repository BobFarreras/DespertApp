package com.deixebledenkaito.despertapp.presentation.screen.paginaPrincipal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.deixebledenkaito.despertapp.domain.model.formState.HomeScreenState
import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository

import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.GetAlarmsUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.alarm.ToggleAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val toggleAlarmUseCase: ToggleAlarmUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(HomeScreenState())
    val uiState: State<HomeScreenState> = _uiState

    init {
        loadUserData()
        loadAlarms()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            authRepository.getCurrentUser()?.let { user ->
                _uiState.value = _uiState.value.copy(
                    userName = user.displayName ?: "Usuari",
                    profileImageUrl = user.photoUrl?.toString()
                )
            }
        }
    }

    // A HomeScreenViewModel
    private fun loadAlarms() {
        viewModelScope.launch {
            getAlarmsUseCase().collect { alarms ->
                _uiState.value = _uiState.value.copy(
                    alarms = alarms.sortedBy { it.hour * 100 + it.minute }
                )
            }
        }
    }

    fun toggleAlarm(enabled: Boolean, alarmId: String) {
        viewModelScope.launch {
            toggleAlarmUseCase(alarmId, enabled)
        }
    }


}