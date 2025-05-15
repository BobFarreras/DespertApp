
package com.deixebledenkaito.despertapp.domain.model


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalTime
import java.util.Calendar

data class Alarm(
    val id: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val label: String = "",

    @get:PropertyName("active_days")
    @set:PropertyName("active_days")
    var activeDays: List<Int> = emptyList(), // 1-7 (DayOfWeek)

    val enabled: Boolean = true,

    @get:PropertyName("created_at")
    val createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("user_id")
    val userId: String = ""
) {
    companion object {
        val DAY_ABBREVIATIONS = listOf("Dl", "Dt", "Dc", "Dj", "Dv", "Ds", "Dg")
    }

    @SuppressLint("DefaultLocale")
    fun getFormattedTime(): String {
        return String.format("%02d:%02d", hour, minute)
    }

    fun getDayNames(): List<String> {
        return activeDays.mapNotNull { dayValue ->
            when (dayValue) {
                in 1..7 -> DAY_ABBREVIATIONS[dayValue - 1]
                else -> null
            }
        }
    }

    fun shouldRingToday(): Boolean {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) // 1-7
        return activeDays.contains(today)
    }

    override fun toString(): String {
        return "Alarma(id='$id', hora=$hour:$minute, etiqueta='$label', " +
                "días=${activeDays.joinToString()}, activa=$enabled)"
    }
}