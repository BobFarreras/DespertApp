package com.deixebledenkaito.despertapp.data

import androidx.room.TypeConverter

// Converters.kt
class Converters {
    // Converteix una llista d'enters a una cadena per emmagatzemar a Room
    @TypeConverter
    fun fromList(list: List<Int>): String = list.joinToString(",")

    // Converteix una cadena a una llista d'enters
    @TypeConverter
    fun toList(data: String): List<Int> =
        if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }
}