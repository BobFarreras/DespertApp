package com.deixebledenkaito.despertapp.ui.screens.challenge

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.deixebledenkaito.despertapp.receiver.AlarmService
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.anime.AnimeChallengeGenerator
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.cultura.CulturaChallengeGenerator
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.utils.AlarmUtils
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.matematiques.MathChallengeGenerator
import kotlinx.coroutines.delay

class AlarmChallengeActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var wakeLock: PowerManager.WakeLock
    private var fromLockScreen = false
    private var volume = 8 // Volum per defecte

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AlarmChallenge", "Iniciant activitat de repte d'alarma")

        CulturaChallengeGenerator.init(applicationContext)
        AnimeChallengeGenerator.init(applicationContext)

        // 1. Configurar so i wake lock
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
            "Anime" -> AnimeChallengeGenerator.generate(testModel)
            else -> MathChallengeGenerator.generate(testModel)
        }

        // 4. Mostrar pantalla de repte
        setContent {
            DespertAppTheme {
                val answerIsCorrect by remember { mutableStateOf<Boolean?>(null) }

                LaunchedEffect(answerIsCorrect) {
                    if (answerIsCorrect == true) {
                        delay(1000)
                        handleCorrectAnswer()
                    }
                }
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
        if (wakeLock.isHeld) wakeLock.release() // ✅ Comprovació abans de fer release()



        // Si venim de pantalla bloquejada, tornar al bloqueig
        // Tornar a bloqueig si cal
        if (fromLockScreen) {
            val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            devicePolicyManager.lockNow()
        }


        finish()

    }


    @SuppressLint("Wakelock", "ImplicitSamInstance")
    override fun onDestroy() {
        mediaPlayer.release()
        if (wakeLock.isHeld) wakeLock.release() // ✅ Comprovació aquí també
        stopService(Intent(this, AlarmService::class.java))
        super.onDestroy()
    }
}