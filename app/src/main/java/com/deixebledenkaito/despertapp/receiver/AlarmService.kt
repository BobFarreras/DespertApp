package com.deixebledenkaito.despertapp.receiver


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent

import android.media.MediaPlayer

import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.ui.screens.challenge.AlarmChallengeActivity

import com.deixebledenkaito.despertapp.utils.AlarmUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import com.deixebledenkaito.despertapp.preferences.AlarmPreferencesManager

//Aquesta classe hereta de Service, una classe base en Android per a serveis que poden funcionar en segon pla, encara que
//l'usuari no estigui interactuant directament amb l'app.
//Un Foreground Service és un servei amb una notificació activa, obligatori a Android 8.0+ si vols que no el matin.
class AlarmService : Service() {
    companion object {
        const val CHANNEL_ID = "alarm_channel"
        const val NOTIFICATION_ID = 1
        var wasNotificationTapped = false
    }

    private var mediaPlayer: MediaPlayer? = null

    private val TIMEOUT_NOTIFICATION_ID = 2
    private val handler = Handler(Looper.getMainLooper())

    private val timeoutRunnable = Runnable {
        if (!wasNotificationTapped) {
            stopAlarmSound()
            cancelAlarmNotification()
            showTimeoutNotification()
            stopSelf()
        }
    }

//    Aquesta funció es crida quan un altre component vol connectar-se al servei mitjançant "binding".
//    Torna null perquè aquest servei no es vincula amb cap component extern, sinó que s'executa de manera autònoma (amb startService() o startForegroundService()).

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Crea una notificació mitjançant createNotification() i crida startForeground(1,
//        notification), fent que el servei s’executi com a servei en primer pla,
//        el que vol dir que l’usuari veu la notificació i és menys probable que el sistema el mati.

        val notification = createNotification(intent)
        startForeground(1, notification)


        // 1. Configurar so i wake lock
        val soundUriString = intent?.getStringExtra("ALARM_SOUND")




        Log.d("AlarmService", "Reproduint so amb URI: $soundUriString")
        startAlarmSound(soundUriString)



        // Iniciar temporitzador de timeout (15s)
        handler.postDelayed(timeoutRunnable, 15_000)

        serviceScope.launch {
            startVibration() // <-- Afegir aquesta línia
        }
        // Aquí pots iniciar el so de l’alarma o altra lògica
        return START_STICKY
    }

    private fun startAlarmSound(soundUriString: String?) {
        serviceScope.launch {
            try {
                mediaPlayer?.release() // si ja n’hi ha un actiu
                mediaPlayer = if (soundUriString != null) {
                    AlarmUtils.playAlarmSound(this@AlarmService, soundId = soundUriString)
                } else {
                    MediaPlayer.create(this@AlarmService, R.raw.alarm)
                }

                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            } catch (e: Exception) {
                Log.e("AlarmService", "Error reproduint el so", e)
                // fallback
                mediaPlayer = MediaPlayer.create(this@AlarmService, R.raw.alarm)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("AlarmService", "onDestroy called")
        serviceScope.cancel()
        handler.removeCallbacks(timeoutRunnable)
        stopAlarmSound() // <- aquí aturem el so realment!
        stopVibration() // <-- Afegir aquesta línia
    }
    suspend fun startVibration() {
        val prefs = AlarmPreferencesManager.loadPreferences(this)
        if (prefs.vibrationEnabled) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(0, 1000, 1000) // Espera 0ms, vibra 1000ms, pausa 1000ms

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, 0) // 0 = no repeteix
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(pattern, -1) // -1 = no repeteix
            }
        }
    }
    private fun stopVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
    private fun stopAlarmSound() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
            mediaPlayer = null
        }

    }
    private fun cancelAlarmNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun showTimeoutNotification() {
        val channelId = "timeout_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        stopAlarmSound()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Timeout",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Temps esgotat")
            .setContentText("S'ha trigat massa a desconnectar l'alarma.")
            .setAutoCancel(true)
            .build()

        manager.notify(TIMEOUT_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarmes actives",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal per mostrar alarmes actives"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotification(intent: Intent?): Notification {
        val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: -1
        val testModel = intent?.getStringExtra("TEST_MODEL") ?: "Bàsic"
        val alarmSound = intent?.getStringExtra("ALARM_SOUND") ?: "default"
        val challengeType = intent?.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"
        val alarmSoundUriString = intent?.getStringExtra("ALARM_SOUND_URI")

        Log.d("AlarmService", "Creant notificació amb canal: $CHANNEL_ID")

        val challengeIntent = Intent(this, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ALARM_ID", alarmId)
            putExtra("ALARM_SOUND", alarmSound)
            putExtra("TEST_MODEL", testModel)
            putExtra("CHALLENGE_TYPE", challengeType)
            putExtra("ALARM_SOUND_URI", alarmSoundUriString)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            challengeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val customLayout = RemoteViews(packageName, R.layout.notification_alarm_custom).apply {

        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

}
