package com.deixebledenkaito.despertapp
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.deixebledenkaito.despertapp.navigation.NavGraph
import com.deixebledenkaito.despertapp.preferences.TemesPreferencesManager
import com.deixebledenkaito.despertapp.preferences.ThemeManager
import com.deixebledenkaito.despertapp.receiver.AlarmService
import com.deixebledenkaito.despertapp.ui.screens.challenge.AlarmChallengeActivity
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
import com.deixebledenkaito.despertapp.ui.screens.components.SystemBarsColorSync
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        if (AlarmService.isAlarmRinging && !AlarmService.wasNotificationTapped) {
            val intent = Intent(this, AlarmChallengeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // Repassa si vols passar aquests extres segons les dades de l'alarma
                putExtra("ALARM_ID", AlarmService.alarmId)
                putExtra("TEST_MODEL", AlarmService.testModel)
                putExtra("CHALLENGE_TYPE", AlarmService.challengeType)
                putExtra("ALARM_SOUND", AlarmService.alarmSound)
                putExtra("ALARM_SOUND_URI", AlarmService.alarmSoundUri)
            }
            startActivity(intent)
            finish()
            return
        }
        // 👉 Carreguem tema des de prefs abans de renderitzar
        lifecycleScope.launch {
            val prefs = TemesPreferencesManager.loadPreferences(this@MainActivity)
            ThemeManager.currentThemeIsDark = prefs.darkEnabled
            Log.d(
                "ThemeDebug",
                "Tema carregat: dark=${prefs.darkEnabled}, light=${prefs.lightEnabled}"
            )

            setContent {
                // Permisos per notificacions si cal
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            1001
                        )
                    }
                }


                DespertAppTheme {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors =  BackgroundApp(ThemeManager.currentThemeIsDark ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                            .padding(top = 6.dp)
                    ) {
                        SystemBarsColorSync(darkTheme = ThemeManager.currentThemeIsDark)
                        val viewModel: AlarmViewModel = hiltViewModel()
                        NavGraph(viewModel = viewModel)

                    }
                }
            }
        }

    }
    override fun onStop() {
        super.onStop()
        // Quan l'app es tanqui, restaurem la icona "normal"
        restaurarIconaNormal()
    }
    private fun restaurarIconaNormal() {
        val pm = applicationContext.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(applicationContext, "com.deixebledenkaito.despertapp.MainActivity"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        pm.setComponentEnabledSetting(
            ComponentName(applicationContext, "com.deixebledenkaito.despertapp.AlarmIconActivityDormida"),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )


    }

}