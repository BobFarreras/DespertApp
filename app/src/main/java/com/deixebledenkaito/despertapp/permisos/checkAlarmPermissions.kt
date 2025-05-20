package com.deixebledenkaito.despertapp.permisos

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Afegir a Constants.kt o a la part superior del fitxer
const val PERMISSION_REQUEST_CODE = 1001


// Funció per verificar i demanar permisos
fun checkAlarmPermissions(activity: Activity, onGranted: () -> Unit) {
    // Verificar permisos per versions modernes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            try {
                activity.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            } catch (e: Exception) {
                Toast.makeText(
                    activity,
                    "Permís necessari per a alarmes exactes",
                    Toast.LENGTH_LONG
                ).show()
            }
            return
        }
    }

    // Verificar permisos de boot completat
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECEIVE_BOOT_COMPLETED),
            PERMISSION_REQUEST_CODE
        )
    } else {
        onGranted()
    }
}