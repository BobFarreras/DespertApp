package com.deixebledenkaito.despertapp.presentation.broadcast

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.deixebledenkaito.despertapp.MainActivity

import com.deixebledenkaito.despertapp.R
import kotlinx.coroutines.NonCancellable.start

class AlarmSoundService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "alarm_service_channel"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("AlarmSoundService", "Servicio creado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmSoundService", "Iniciando servicio...")

        // Crear notificación foreground
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Iniciar sonido de alarma
        startAlarmSound()

        return START_STICKY
    }

    private fun createNotification(): Notification {
        Log.d("AlarmSoundService", "Creando notificación foreground")

        // Crear canal (para Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Servicio de Alarma",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canal para el servicio de alarma en primer plano"
            }

            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

        // Intent para abrir la app al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarma sonando")
            .setContentText("Toca para abrir la aplicación")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startAlarmSound() {
        Log.d("AlarmSoundService", "Preparando sonido de alarma...")

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm).apply {
                isLooping = true
                setVolume(1.0f, 1.0f)
                setOnErrorListener { mp, what, extra ->
                    Log.e("AlarmSoundService", "Error en MediaPlayer: $what, $extra")
                    false
                }
                start()
            }
            Log.d("AlarmSoundService", "Sonido de alarma iniciado")
        } catch (e: Exception) {
            Log.e("AlarmSoundService", "Error al iniciar sonido: ${e.message}")
        }
    }

    override fun onDestroy() {
        Log.d("AlarmSoundService", "Deteniendo servicio...")
        mediaPlayer?.let {
            it.stop()
            it.release()
            Log.d("AlarmSoundService", "MediaPlayer liberado")
        }
        mediaPlayer = null
        super.onDestroy()
    }
}