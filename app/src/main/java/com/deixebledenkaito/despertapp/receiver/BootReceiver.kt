package com.deixebledenkaito.despertapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completat. Es reprogramaran les alarmes.")
            // Programar treball per a reprogramar totes les alarmes
            val workRequest = OneTimeWorkRequestBuilder<AlarmRescheduleWorker>()
                .setInitialDelay(9, TimeUnit.MINUTES) // Esperar 9 minuts després del boot
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}