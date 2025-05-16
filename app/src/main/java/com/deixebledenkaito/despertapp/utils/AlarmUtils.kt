package com.deixebledenkaito.despertapp.utils

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build

import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator

import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.ui.screens.AlarmChallengeActivity




import android.util.Log


object AlarmUtils {

    private const val WAKE_TAG = "DespertApp::AlarmWakeLock"
    private const val MAX_WAKE_TIME_MS = 10 * 30 * 1000L // 10 minuts
    private const val VIBRATION_TIME_MS = 1000L // 1 segon

    /**
     * Reprodueix l'àudio de l'alarma al màxim volum.
     * Retorna el MediaPlayer actiu.
     */
    fun playAlarmSound(context: Context): MediaPlayer {
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            AudioManager.FLAG_SHOW_UI
        )

        return MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_ALARM)
            setDataSource(context, Uri.parse("android.resource://${context.packageName}/${R.raw.alarm}"))
            isLooping = true
            prepare()
            start()
        }
    }

    /**
     * Fa vibrar el dispositiu durant 1 segon.
     */
    fun vibratePhone(context: Context) {
        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(1000, 1000),
                    intArrayOf(255, 0),
                    0
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(1000, 1000), 0)
        }
    }

    /**
     * Retorna un WakeLock actiu per mantenir el dispositiu despert.
     */
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

    /**
     * Llança l'activitat del repte matemàtic.
     */
    fun launchAlarmChallenge(context: Context) {
        val intent = Intent(context, AlarmChallengeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Allibera recursos del MediaPlayer de forma segura.
     */
    fun stopAndReleasePlayer(mediaPlayer: MediaPlayer?) {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AlarmUtils", "Error alliberant MediaPlayer: ${e.message}")
        }
    }


}
