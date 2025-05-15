package com.deixebledenkaito.despertapp.domain.model


import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalTime

data class Alarm(
    val id: String = "",  // Firebase document ID
    val hour: Int = 0,
    val minute: Int = 0,
    val label: String = "",
    @get:PropertyName("active_days")
    @set:PropertyName("active_days")
    var activeDays: List<Int> = emptyList(), // Valors DayOfWeek (1-7)
    val enabled: Boolean = true,
    @get:PropertyName("created_at")
    val createdAt: Timestamp = Timestamp.now(),
    @get:PropertyName("user_id")
    val userId: String = ""  // ID de l'usuari que ha creat l'alarma
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

    fun getFormattedTime(): String {
        return String.format("%02d:%02d", hour, minute)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayNames(): List<String> {
        return activeDays.map { dayValue ->
            when (dayValue) {
                0-> "Dl"
                1-> "Dt"
                2 -> "Dc"
                3 -> "Dj"
                4 -> "Dv"
                5 -> "Ds"
                6 -> "Dg"
                else -> ""
            }
        }
    }
}