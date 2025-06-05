package com.deixebledenkaito.despertapp.receiver


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
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
import com.deixebledenkaito.despertapp.preferences.AlarmPreferencesManager

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.delay

//Aquesta classe hereta de Service, una classe base en Android per a serveis que poden funcionar en segon pla, encara que
//l'usuari no estigui interactuant directament amb l'app.
//Un Foreground Service és un servei amb una notificació activa, obligatori a Android 8.0+ si vols que no el matin.
class AlarmService : Service() {

    companion object {
        const val CHANNEL_ID = "alarm_channel"
        const val TIMEOUT_CHANNEL = "timeout_channel"
        const val NOTIFICATION_ID = 1
        const val TIMEOUT_NOTIFICATION_ID = 2
        var wasNotificationTapped = false
        private const val TAG = "AlarmService"
        var isAlarmRinging = false

        var alarmId: Int = -1
        var testModel: String = "Bàsic"
        var challengeType: String = "Matemàtiques"
        var alarmSound: String = "default"
        var alarmSoundUri: String? = null
    }

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null

    // Executat després de 30s si l'usuari no interactua amb la notificació
    private val timeoutRunnable = Runnable {
        if (!wasNotificationTapped) {
            Log.d(TAG, "⏰ Timeout activat. S'atura l'alarma.")
            stopAlarmSound()
            cancelAlarmNotification()
            showTimeoutNotification()
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private fun acquireWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AlarmService::WakeLock"
        ).apply {
            setReferenceCounted(false)
            acquire(10 * 60 * 1000L) // 10 minuts màxim
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.takeIf { it.isHeld }?.release()
        wakeLock = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Crida immediata a startForeground amb una notificació vàlida
        val notification = createNotification(intent)
        startForeground(NOTIFICATION_ID, notification)

        isAlarmRinging = true
        activarIconaAlarma()

        // 1. Configurar so i wake lock
        alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: -1
        testModel = intent?.getStringExtra("TEST_MODEL") ?: "Bàsic"
        challengeType = intent?.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"
        alarmSound = intent?.getStringExtra("ALARM_SOUND") ?: "default"
        alarmSoundUri = intent?.getStringExtra("ALARM_SOUND_URI")

        Log.d(TAG, "Reproduint so amb URI: $alarmSound")

        acquireWakeLock() // ✅ Nova línia
        startAlarmSound(alarmSound)

        // Iniciar temporitzador de timeout (15s)

        serviceScope.launch {
            delay(30_000)

            if (!wasNotificationTapped) {
                Log.d(TAG, "⏰ Timeout activat. S'atura l'alarma.")
                activarIconaDormint()
                stopAlarmSound()
                cancelAlarmNotification()
                showTimeoutNotification()
                stopSelf()
            }
        }
        serviceScope.launch {startVibration() }

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
                Log.e(TAG, "Error reproduint el so", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                // fallback
                mediaPlayer = MediaPlayer.create(this@AlarmService, R.raw.alarm)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        serviceScope.cancel()
        handler.removeCallbacks(timeoutRunnable)
        stopAlarmSound() // <- aquí aturem el so realment!
        stopVibration() // <-- aqui aturem la vibracioó
        releaseWakeLock() // ✅ Nova línia

        isAlarmRinging = false
        wasNotificationTapped = false
    }
    suspend fun startVibration() {
        val prefs = AlarmPreferencesManager.loadPreferences(this)
        if (prefs.vibrationEnabled) {
            val vibrator = getSystemService(Vibrator::class.java)
            val pattern = longArrayOf(0, 1000, 1000)

            val effect = VibrationEffect.createWaveform(pattern, 0)
            vibrator.vibrate(effect)
        }
    }
    private fun stopVibration() {
        val vibrator = getSystemService(Vibrator::class.java)
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
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun showTimeoutNotification() {
        val manager = getSystemService(NotificationManager::class.java)

        // Important: ja tenim canal creat, no cal tornar-lo a crear si existeix
        val notification = NotificationCompat.Builder(this, TIMEOUT_CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Temps esgotat")
            .setContentText("S'ha trigat massa a desconnectar l'alarma.")
            .setAutoCancel(true)
            .build()

        manager.notify(TIMEOUT_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)

        // Canal per alarmes actives
        val alarmChannel = NotificationChannel(
            CHANNEL_ID,
            "Alarmes actives",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal per mostrar alarmes actives"
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        // Canal per timeout (separat per comportament diferent)
        val timeoutChannel = NotificationChannel(
            TIMEOUT_CHANNEL,
            "Timeout d'alarma",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal per notificació de temps esgotat"
        }

        manager.createNotificationChannel(alarmChannel)
        manager.createNotificationChannel(timeoutChannel)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotification(intent: Intent?): Notification {
        val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: -1
        val testModel = intent?.getStringExtra("TEST_MODEL") ?: "Bàsic"
        val alarmSound = intent?.getStringExtra("ALARM_SOUND") ?: "default"
        val challengeType = intent?.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"
        val alarmSoundUriString = intent?.getStringExtra("ALARM_SOUND_URI")

        Log.d(TAG, "Creant notificació amb canal: $CHANNEL_ID")

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
        val customLayout = RemoteViews(packageName, R.layout.notification_alarm_custom)

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
    private fun activarIconaAlarma() {
        val pm = applicationContext.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(applicationContext, "com.deixebledenkaito.despertapp.AlarmIconActivity"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP

        )

        pm.setComponentEnabledSetting(
            ComponentName(applicationContext, "com.deixebledenkaito.despertapp.MainActivity"),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )


    }

    private fun activarIconaDormint(){
        val pm = applicationContext.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(applicationContext, "com.deixebledenkaito.despertapp.AlarmIconActivityDormida"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP

        )
        pm.setComponentEnabledSetting(
            ComponentName(applicationContext, "com.deixebledenkaito.despertapp.AlarmIconActivity"),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }


}
