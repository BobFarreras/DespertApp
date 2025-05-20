package com.deixebledenkaito.despertapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import com.deixebledenkaito.despertapp.R

object AlarmUtils {

    private const val WAKE_TAG = "DespertApp::AlarmWakeLock"

    /**
     * Reprodueix l'àudio de l'alarma al màxim volum.
     * Retorna el MediaPlayer actiu.
     */
    fun playAlarmSound(context: Context, volume: Int, soundId: String = "default"): MediaPlayer {
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)

        //AIXÒ ET RETORNA EL VOLUM DEL MOVIL
        val adjustedVolume = volume.coerceIn(0, maxVolume)

        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            0, //AMB 0 NO MOSTRA LA BARRA DEL VOLUM
            0 // <-- Aquesta línia evita mostrar la barra de volum
        )

        // Obtenim el recurs de so segons l'ID
        val soundResource = when (soundId) {
            "bebe" -> R.raw.bebe
            "bolaDeDrac" -> R.raw.boladedrac
            "insultsCatala" -> R.raw.insults
            "SoldatAlarma" -> R.raw.soldatalarma
            else -> R.raw.alarm // So per defecte
        }

        return MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_ALARM)
            setDataSource(context, Uri.parse("android.resource://${context.packageName}/$soundResource"))
            isLooping = true
            prepare()
            start()
        }
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