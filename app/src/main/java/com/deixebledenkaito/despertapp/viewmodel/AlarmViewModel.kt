package com.deixebledenkaito.despertapp.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val alarms: StateFlow<List<AlarmEntity>> = repository.allAlarms
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    init {
        viewModelScope.launch {
            delay(500) // ← espera un mínim perquè el loader es vegi
            repository.allAlarms.collect { currentAlarms ->
                Log.d("AlarmViewModel", "Alarmes actualitzades: ${currentAlarms.size} alarmes")
                _isLoading.value = false
            }
        }

    }

    // Funció unificada per afegir/actualitzar alarmes
    private suspend fun upsertAlarm(alarm: AlarmEntity) {
        Log.d("AlarmViewModel", "Actualitzant/afegint alarma ID: ${alarm.id}")
        repository.insert(alarm)
        if (alarm.isActive) {
            scheduleAlarm(alarm)
        } else {
            cancelAlarm(alarm.id)
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

    // Funció unificada per canviar estat
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

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(alarm: AlarmEntity) {
        if (!alarm.isActive) {
            Log.d("AlarmViewModel", "Alarma ${alarm.id} desactivada, no es programa.")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("CHALLENGE_TYPE", alarm.challengeType)
            putExtra("TEST_MODEL", alarm.testModel)
            putExtra("ALARM_SOUND", alarm.alarmSound)
            putExtra("REPEAT_TYPE", alarm.repeatType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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

        // Per a totes les alarmes (recurrents o no) fem servir setExactAndAllowWhileIdle
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("AlarmViewModel", "Alarma programada: ${alarm.id} a ${alarm.hour}:${alarm.minute} " +
                "(Model: ${alarm.testModel}, Tipus: ${alarm.repeatType})")

        // Si és recurrent, programem la següent execució des del receiver
    }
}