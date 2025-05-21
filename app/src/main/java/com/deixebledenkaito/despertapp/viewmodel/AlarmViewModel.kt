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

    // Llista d'alarmes com a StateFlow
    val alarms: StateFlow<List<AlarmEntity>> = repository.allAlarms
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    init {
        // Inicialitza la càrrega de dades
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
    fun scheduleAlarm(alarm: AlarmEntity) {
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

            // Afegim flags addicionals per a millorar la fiabilitat
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        }

        // Utilitzem FLAG_IMMUTABLE només per a versions modernes
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            flags
        )

        val calendar = calculateNextAlarmTime(alarm)
        Log.d("AlarmViewModel", "Alarma ${alarm.id} programada per ${calendar.time}")
        // Configuració per a versions modernes d'Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                // Sol·licitar permís si no el tenim
                val intent = Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

        Log.d("AlarmViewModel", "Alarma programada: ${alarm.id} a ${alarm.hour}:${alarm.minute}")
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