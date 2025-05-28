package com.deixebledenkaito.despertapp.receiver

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deixebledenkaito.despertapp.data.AlarmDatabase
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository
import com.deixebledenkaito.despertapp.viewmodel.AlarmScheduler
import kotlinx.coroutines.delay

// Crear una classe Worker per a reprogramacions
//Un Worker (en aquest cas un CoroutineWorker) és una classe usada amb WorkManager per executar tasques en segon pla, de forma fiable i tolerant a reinicis del dispositiu.
class AlarmRescheduleWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = AlarmRepository(AlarmDatabase.getDatabase(applicationContext).alarmDao())
            val alarms = repo.getActiveAlarms()
            val scheduler = AlarmScheduler(applicationContext)

            alarms.forEach {
                scheduler.schedule(it)
                delay(100)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("AlarmRescheduleWorker", "Error reprogramant", e)
            Result.retry()
        }
    }
}
