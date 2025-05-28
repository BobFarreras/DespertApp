package com.deixebledenkaito.despertapp.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.receiver.AlarmReceiver
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {


    private val alarmScheduler = AlarmScheduler(context)

    // Llista d'alarmes com a StateFlow
    val alarms = repository.allAlarms // sense stateIn


    init {
        observeAlarms()

    }

    private fun observeAlarms() {
        viewModelScope.launch {
            repository.allAlarms.collectLatest { currentAlarms ->

                // Evitem reprogramar si no hi ha canvis reals
                currentAlarms.forEach { alarm ->
                    if (alarm.isActive) {
                        Log.d("AlarmViewModel", "Reprogramant alarma ID: ${alarm.id}")
                        alarmScheduler.schedule(alarm)
                    } else {
                        cancelAlarm(alarm.id)
                    }
                }


            }
        }
    }



    // Funció unificada per afegir/actualitzar alarmes
    private suspend fun upsertAlarm(alarm: AlarmEntity) {
        repository.insert(alarm)
        if (alarm.isActive) {
//            alarmScheduler.schedule(alarm)
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
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            data = "alarm://$alarmId".toUri()
            action = "action.$alarmId"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmViewModel", "Alarma cancel·lada ID: $alarmId")
    }

}