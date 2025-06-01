package com.deixebledenkaito.despertapp.ui.screens.challenge

import android.annotation.SuppressLint
import android.app.KeyguardManager

import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
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

import com.deixebledenkaito.despertapp.data.AlarmDatabase
import com.deixebledenkaito.despertapp.receiver.AlarmService
import com.deixebledenkaito.despertapp.repositroy.AlarmRepository
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.angles.AnglesChallengeGenerator
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.anime.AnimeChallengeGenerator
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.cultura.CulturaChallengeGenerator
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.utils.AlarmUtils
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.matematiques.MathChallengeGenerator
import com.deixebledenkaito.despertapp.viewmodel.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmChallengeActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var wakeLock: PowerManager.WakeLock
    private var fromLockScreen = false


    @SuppressLint("ImplicitSamInstance")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AlarmChallenge", "Iniciant activitat de repte d'alarma")

        // Marca la notificació com a clicada
        AlarmService.wasNotificationTapped = true

        // Iniciar generadors
        CulturaChallengeGenerator.init(applicationContext)
        AnimeChallengeGenerator.init(applicationContext)
        AnglesChallengeGenerator.init(applicationContext)

        // Parar servei perquè activitat control·li el so
        stopService(Intent(this, AlarmService::class.java))

        // Assegurar pantalla activa
        wakeLock = AlarmUtils.acquireWakeLock(this)

        // Configuració per mostrar sobre el bloqueig
        configurarPantallaBloqueig()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //        AQUI AGAFEM EL TIPUS DE MODEL QUE TE L'ALARMA'
        val testModel = intent.getStringExtra("TEST_MODEL") ?: "Bàsic"
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE") ?: "Matemàtiques"
        Log.d("AlarmChallenge", "Tipus de desafiament: $challengeType")

        val finalChallengeType = if (challengeType == "Aleatori") {
            listOf("Matemàtiques", "Cultura Catalana", "Anime", "Angles").random()
        } else {
            challengeType
        }
        Log.d("AlarmChallenge", "Tipus de desafiament: $finalChallengeType")

        val question = when (finalChallengeType) {
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
                    onCorrect = ::handleCorrectAnswer,
                    onSnooze = ::handleSnooze
                )
            }
        }
    }
    private fun configurarPantallaBloqueig() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }
//    FUNCIO QUE COMPROVA LA LOGICA DE SI L'USUARI A ASERTAT!'
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

    //    FUNCIO QUE POSPOSA L'ALARMA 10 MIN'
    private fun handleSnooze() {

        if (wakeLock.isHeld) wakeLock.release()
        mediaPlayer.stop()
        mediaPlayer.release()


        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        if (alarmId == -1) {
            Toast.makeText(this, "Error: ID d'alarma no vàlid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Recuperar i modificar l'alarma a la base de dades
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AlarmDatabase.getDatabase(this@AlarmChallengeActivity).alarmDao()
            val repo = AlarmRepository(dao)
            val alarm = repo.getAlarmById(alarmId)
            Log.d("AlarmChallenge", "Alarma recuperada: $alarm")
            if (alarm != null) {
                val snoozedTime = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    add(Calendar.MINUTE, 1)
                }
                Log.d("AlarmChallenge", "Alarma posposada a: $snoozedTime")
                val updatedAlarm = alarm.copy(
                    hour = snoozedTime.get(Calendar.HOUR_OF_DAY),
                    minute = snoozedTime.get(Calendar.MINUTE),
                    isActive = true
                )

                repo.update(updatedAlarm) // Això desencadena el Flow per refrescar la UI

                AlarmScheduler(this@AlarmChallengeActivity).schedule(updatedAlarm)
            }
        }

        runOnUiThread {
            Toast.makeText(this, "Alarma posposada per 10 minuts", Toast.LENGTH_SHORT).show()
            finish()
        }
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
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }

        if (wakeLock.isHeld) {
            wakeLock.release()
        }

        stopService(Intent(this, AlarmService::class.java))
        super.onDestroy()
    }

}