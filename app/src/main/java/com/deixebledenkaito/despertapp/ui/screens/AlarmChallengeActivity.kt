package com.deixebledenkaito.despertapp.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.utils.AlarmUtils
import com.deixebledenkaito.despertapp.utils.MathChallengeGenerator

class AlarmChallengeActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialitzar els recursos
        wakeLock = AlarmUtils.acquireWakeLock(this)
        mediaPlayer = AlarmUtils.playAlarmSound(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        AlarmUtils.vibratePhone(this)

        setContent {
            DespertAppTheme {
                val question = remember { MathChallengeGenerator.generate() }

                AlarmChallengeScreen(
                    question = question,
                    onCorrect = {
                        // Aturem tot immediatament
                        stopAlarmResources()
                        finish()
                    }
                )
            }
        }
    }

    private fun stopAlarmResources() {
        try {
            mediaPlayer.stop()
            mediaPlayer.release()
            vibrator.cancel()
            wakeLock.release()
        } catch (e: Exception) {
            Log.e("AlarmChallenge", "Error alliberant recursos", e)
        }
    }

    override fun onDestroy() {
        stopAlarmResources()
        super.onDestroy()
    }
}