
package com.deixebledenkaito.despertapp.domain.model


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalTime

data class Alarm(
    val id: String = "",  // ID del document a Firestore
    val hour: Int = 0,
    val minute: Int = 0,
    val label: String = "",

    @get:PropertyName("active_days")
    @set:PropertyName("active_days")
    var activeDays: List<Int> = emptyList(), // Valors entre 1-7 (DayOfWeek)

    val enabled: Boolean = true,

    @get:PropertyName("created_at")
    val createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("user_id")
    val userId: String = ""
) {

    // Convertir hora i minut a LocalTime per mostrar o comparar

    fun toLocalTime(): LocalTime = LocalTime.of(hour, minute)

    // Format simple (es pot mostrar com "07:30")
    @SuppressLint("DefaultLocale")
    fun getFormattedTime(): String {
        return String.format("%02d:%02d", hour, minute)
    }

    // Dies actius en format curt ("Dl", "Dt", ...)

    fun getDayNames(): List<String> {
        return activeDays.map { dayValue ->
            when (dayValue) {
                0 -> "Dl"
                1 -> "Dt"
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
