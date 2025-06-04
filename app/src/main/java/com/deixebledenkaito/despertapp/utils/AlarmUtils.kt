package com.deixebledenkaito.despertapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.preferences.AlarmPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds.CustomSoundManager

object AlarmUtils {

    private const val TAG = "AlarmUtils"
    private const val WAKE_TAG = "DespertApp::AlarmWakeLock"

    /**
     * Reprodueix l'àudio de l'alarma al màxim volum.
     * Retorna el MediaPlayer actiu.
     */
    @SuppressLint("ObsoleteSdkInt")
    suspend fun playAlarmSound(context: Context, soundId: String = "default"): MediaPlayer {
        val prefs = AlarmPreferencesManager.loadPreferences(context)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val volumeLevel = (prefs.volume / 100f * maxVolume).toInt()

        if (!prefs.increasingVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumeLevel, 0)
        }

        val mediaPlayer = MediaPlayer().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Método modern (API 21+)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            } else {
                // Método antic (solo para compatibilidad)
                @Suppress("DEPRECATION")
                setAudioStreamType(AudioManager.STREAM_ALARM)
            }
            isLooping = true
        }



        try {
            if (soundId.startsWith("content://")) {
                Log.d(TAG, "🔊 És un so personalitzat amb URI: $soundId")

                val uri = soundId.toUri()
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                mediaPlayer.setDataSource(context, uri)
                mediaPlayer.prepare()

                // 🔍 Comprovem el startTime guardat
                val customSounds = CustomSoundManager.getCustomSounds(context)
                Log.d(TAG, "🔎 Trobats ${customSounds.size} sons personalitzats")

                val matched = customSounds.find { it.uriString == soundId }
                val startTimeMs = matched?.startTimeMs ?: 0L
                Log.d(TAG, "🎯 StartTime per a aquest so: $startTimeMs ms")

                mediaPlayer.seekTo(startTimeMs.toInt()) // Exigeix Int
                mediaPlayer.start()

            } else {
                Log.d(TAG, "🔊 És un so integrat (resource): $soundId")

                val soundResource = when (soundId) {
                    "Piano" -> R.raw.piano
                    "bolaDeDrac" -> R.raw.boladedrac
                    "insultsCatala" -> R.raw.insults
                    "SoldatAlarma" -> R.raw.soldatalarma
                    "Vegeta" -> R.raw.vegeta
                    "LinkinPark" -> R.raw.linkpark
                    "ImagineDragons" -> R.raw.imagindragons
                    "RumbaMediterrani" -> R.raw.rumbamediterrani
                    "PianoDos" -> R.raw.pianodos
                    else -> R.raw.alarm
                }

                val uri = "android.resource://${context.packageName}/$soundResource".toUri()
                mediaPlayer.setDataSource(context, uri)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error reproduint el so", e)
        }

        // Augment progressiu del volum
        if (prefs.increasingVolume) {
            CoroutineScope(Dispatchers.Default).launch {
                for (i in 1..volumeLevel) {
                    delay(500)
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, i, 0)
                }
            }
        }
        val pattern = longArrayOf(0, 1000, 500) // Patró millorat: 0ms espera, 1s vibració, 0.5s pausa

        // Vibració
        Log.d(TAG, "Vibrate: ${prefs.vibrationEnabled}")
        if (prefs.vibrationEnabled) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(Vibrator::class.java)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
            }

            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    val effect = VibrationEffect.createWaveform(pattern, 0)
                    it.vibrate(effect)
                }
            } ?: Log.w(TAG, "No s'ha pogut obtenir Vibrator per a vibrar")
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