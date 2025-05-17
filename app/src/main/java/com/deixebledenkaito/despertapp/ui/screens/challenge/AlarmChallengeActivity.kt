package com.deixebledenkaito.despertapp.ui.screens.challenge

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.deixebledenkaito.despertapp.receiver.AlarmService
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.utils.AlarmUtils
import com.deixebledenkaito.despertapp.utils.MathChallengeGenerator

class AlarmChallengeActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var wakeLock: PowerManager.WakeLock
    private var fromLockScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuració per mostrar sobre el bloqueig
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        fromLockScreen = intent.getBooleanExtra("FROM_LOCK_SCREEN", false)

        // Inicialitzar recursos
        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).run {
            newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "DespertApp::AlarmWakeLock"
            ).apply { acquire(10 * 60 * 1000L /*10 minuts*/) }
        }

        mediaPlayer = AlarmUtils.playAlarmSound(this, 1)
        AlarmUtils.vibratePhone(this)

        setContent {
            DespertAppTheme {

                AlarmChallengeScreen(
                    question = remember { MathChallengeGenerator.generate() },
                    onCorrect = ::handleCorrectAnswer
                )
            }
        }
    }

    private fun handleCorrectAnswer() {
        // Aturar recursos
        mediaPlayer.stop()
        mediaPlayer.release()
        wakeLock.release()

        // Si venim de pantalla bloquejada, tornar al bloqueig
        if (fromLockScreen) {
            val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            devicePolicyManager.lockNow()
        }

        // Aturar el servei
        stopService(Intent(this, AlarmService::class.java))
        finish()
    }


    override fun onDestroy() {
        mediaPlayer.release()
        wakeLock.release()
        stopService(Intent(this, AlarmService::class.java))
        super.onDestroy()
    }
}