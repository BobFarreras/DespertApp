package com.deixebledenkaito.despertapp.receiver

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deixebledenkaito.despertapp.data.AlarmDatabase
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel
import kotlinx.coroutines.delay

// Crear una classe Worker per a reprogramacions
class AlarmRescheduleWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = AlarmRepository(AlarmDatabase.getDatabase(applicationContext).alarmDao())
            val alarms = repo.getActiveAlarms()

            alarms.forEach { alarm ->
                // Utilitzem una nova instància del ViewModel
                val viewModel = AlarmViewModel(repo, applicationContext)
                viewModel.scheduleAlarm(alarm)

                // Petita pausa per evitar sobrecarrega
                delay(100)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("AlarmRescheduleWorker", "Error en reprogramar alarmes", e)
            Result.retry() // Reintentar en cas d'error
        }
    }
}