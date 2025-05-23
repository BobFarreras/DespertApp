package com.deixebledenkaito.despertapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.preferences.AlarmPreferencesManager
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds.AlarmSound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.Calendar

object AlarmUtils {

    private const val WAKE_TAG = "DespertApp::AlarmWakeLock"

    /**
     * Reprodueix l'àudio de l'alarma al màxim volum.
     * Retorna el MediaPlayer actiu.
     */
    suspend fun playAlarmSound(context: Context, soundId: String = "default"): MediaPlayer {
        val prefs = AlarmPreferencesManager.loadPreferences(context)

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val volumeLevel = (prefs.volume / 100f * maxVolume).toInt()

        if (!prefs.increasingVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumeLevel, 0)
        }

        val soundResource = when (soundId) {
            "bebe" -> R.raw.bebe
            "bolaDeDrac" -> R.raw.boladedrac
            "insultsCatala" -> R.raw.insults
            "SoldatAlarma" -> R.raw.soldatalarma
            "Vegeta" -> R.raw.vegeta
            "LinkinPark" -> R.raw.linkpark
            "ImagineDragons" -> R.raw.imagindragons
            else -> R.raw.alarm
        }

        val mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_ALARM)
            setDataSource(context, Uri.parse("android.resource://${context.packageName}/$soundResource"))
            isLooping = true
            prepare()
            start()
        }

        if (prefs.increasingVolume) {
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 1..volumeLevel) {
                    delay(500)
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, i, 0)
                }
            }
        }

        if (prefs.vibrationEnabled) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(1000)
            }
        }

        return mediaPlayer
    }
    /**
     * Retorna un WakeLock actiu per mantenir el dispositiu despert.
     */
    @SuppressLint("Wakelock")
    fun acquireWakeLock(context: Context): PowerManager.WakeLock {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP,
                WAKE_TAG
            ).apply {
                acquire(10 * 60 * 1000L /*10 minuts*/)
            }
        }
    }

    
}