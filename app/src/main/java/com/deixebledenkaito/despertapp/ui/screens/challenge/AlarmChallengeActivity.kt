package com.deixebledenkaito.despertapp.ui.screens.challenge

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.deixebledenkaito.despertapp.receiver.AlarmService
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.angles.AnglesChallengeGenerator
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.anime.AnimeChallengeGenerator
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.cultura.CulturaChallengeGenerator
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.utils.AlarmUtils
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.matematiques.MathChallengeGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        AnglesChallengeGenerator.init(applicationContext)

        // 1. Configurar so i wake lock
        val alarmSound = intent.getStringExtra("ALARM_SOUND") ?: "default"

        // Configurar el mediaPlayer amb el so correcte
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                mediaPlayer = AlarmUtils.playAlarmSound(this@AlarmChallengeActivity, soundId = alarmSound)
            } catch (e: Exception) {
                Log.e("AlarmChallenge", "Error en reproduir el so de l'alarma", e)
            }
        }
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
            "Angles" -> AnglesChallengeGenerator.generate(testModel)
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

//AMAGAR LA BOTTOM BAR DEL MOBIL
    @Suppress("DEPRECATION")
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        hideSystemUI()
    }
    @SuppressLint("MissingSuperCall")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        // No fem res per bloquejar la sortida amb el botó enrere
    }

    @SuppressLint("Wakelock", "ImplicitSamInstance")
    override fun onDestroy() {
        mediaPlayer.release()
        if (wakeLock.isHeld) wakeLock.release() // ✅ Comprovació aquí també
        stopService(Intent(this, AlarmService::class.java))
        super.onDestroy()
    }

}