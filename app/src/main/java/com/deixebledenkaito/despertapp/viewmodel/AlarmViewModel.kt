package com.deixebledenkaito.despertapp.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.receiver.AlarmReceiver
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // Estat de càrrega
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val alarmScheduler = AlarmScheduler(context)

    // Llista d'alarmes com a StateFlow
    val alarms: StateFlow<List<AlarmEntity>> = repository.allAlarms
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        viewModelScope.launch {
            repository.allAlarms.collect { currentAlarms ->
                _isLoading.value = false
                // Només reprogramem alarmes quan realment cal (inicialització o canvis rellevants)
                if (currentAlarms != alarms.value) {
                    currentAlarms.filter { it.isActive }.forEach { alarm ->
                        Log.d("AlarmViewModel", "Reprogramant alarma ID: ${alarm.id}")
                        alarmScheduler.schedule(alarm)
                    }
                }
            }
        }
    }


    // Funció unificada per afegir/actualitzar alarmes
    private suspend fun upsertAlarm(alarm: AlarmEntity) {
        repository.insert(alarm)
        if (alarm.isActive) {
            alarmScheduler.schedule(alarm)
        } else {
            Log.d("AlarmViewModel", "  private suspend fun upsertAlarm(alarm: AlarmEntity) nose que em de fer")

        }
    }

    // Funció pública per afegir nova alarma
    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            Log.d("AlarmViewModel", "Afegint nova alarma: $alarm")
            upsertAlarm(alarm)
        }
    }

    // Funció per eliminar alarma
    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            Log.d("AlarmViewModel", "Eliminant alarma ID: ${alarm.id}")
            repository.delete(alarm)
            cancelAlarm(alarm.id)
        }
    }

    // Funció unificada per ativar desactivar alarma
    fun toggleAlarmActive(alarm: AlarmEntity, isActive: Boolean) {
        viewModelScope.launch {
            upsertAlarm(alarm.copy(isActive = isActive))
        }
    }

    // Cancel·lar alarma al sistema
    private fun cancelAlarm(alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmViewModel", "Alarma cancel·lada ID: $alarmId")
    }

    private fun calculateNextAlarmTime(alarm: AlarmEntity): Calendar {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si l'hora ja ha passat avui, programem per demà
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar
    }
}