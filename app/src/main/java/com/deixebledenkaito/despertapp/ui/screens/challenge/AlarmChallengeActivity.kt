package com.deixebledenkaito.despertapp.ui.screens.challenge

import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.deixebledenkaito.despertapp.receiver.AlarmService
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.CulturaChallengeGenerator
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.utils.AlarmUtils
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.MathChallengeGenerator

class AlarmChallengeActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var wakeLock: PowerManager.WakeLock
    private var fromLockScreen = false
    private var volume = 8 // Volum per defecte

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AlarmChallenge", "Iniciant activitat de repte d'alarma")

        CulturaChallengeGenerator.init(applicationContext)

        // Obtenir el so de l'intent
        val alarmSound = intent.getStringExtra("ALARM_SOUND") ?: "default"


        // Configurar el mediaPlayer amb el so correcte
        mediaPlayer = AlarmUtils.playAlarmSound(this, volume, alarmSound)
        wakeLock = AlarmUtils.acquireWakeLock(this)

        // Configuració per mostrar sobre el bloqueig
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //        AQUI AGAFEM EL TIPUS DE MODEL QUE TE L'ALARMA'
        val testModel = intent.getStringExtra("TEST_MODEL") ?: "Bàsic"
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"
        Log.d("AlarmChallenge", "Tipus de desafiament: $challengeType")

        val question = when (challengeType) {
            "Matemàtiques" -> MathChallengeGenerator.generate(testModel)
            "Cultura Catalana" -> CulturaChallengeGenerator.generate(testModel)
            else -> MathChallengeGenerator.generate(testModel)
        }
        setContent {
            DespertAppTheme {
                AlarmChallengeScreen(
                    question = question,
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