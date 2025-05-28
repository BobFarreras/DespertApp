package com.deixebledenkaito.despertapp.viewmodel
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.deixebledenkaito.despertapp.data.AlarmEntity
import com.deixebledenkaito.despertapp.receiver.AlarmReceiver
import java.util.Calendar
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri

class AlarmScheduler(private val context: Context) {

    fun schedule(alarm: AlarmEntity) {
        if (!alarm.isActive) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("CHALLENGE_TYPE", alarm.challengeType)
            putExtra("TEST_MODEL", alarm.testModel)
            putExtra("ALARM_SOUND", alarm.alarmSound)
            putExtra("NOM_ALARMA", alarm.name)
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)

            // Assegura que cada PendingIntent sigui únic
            data = "alarm://${alarm.id}".toUri()
            action = "action.${alarm.id}"  // Acció única per a cada alarma
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,  // RequestCode únic per a cada alarma
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("AlarmScheduler", "Alarma programada ID: ${alarm.id} total $alarm")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Calcula el proper temps d'activació basat en els dies de la setmana si és recurrent
        val calendar = calculateNextAlarmTime(alarm)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            val permissionIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(permissionIntent)
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("AlarmScheduler", "Alarma programada ID: ${alarm.id} per a ${calendar.time}")
    }

    private fun calculateNextAlarmTime(alarm: AlarmEntity): Calendar {
        Log.d("AlarmScheduler", "Programant alarma ID: ${alarm.id}")
        Log.d("AlarmScheduler", "Dies seleccionats: ${alarm.daysOfWeek.joinToString()}")
        Log.d("AlarmScheduler", "Hora: ${alarm.hour}:${alarm.minute}")
        Log.d("AlarmScheduler", "Recurrent: ${alarm.isRecurring}")

        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si l'hora ja ha passat avui, programem per demà
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

    }


}
