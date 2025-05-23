package com.deixebledenkaito.despertapp.ui.screens.components

import java.time.Duration

fun formatarDuracio(duration: Duration): String {
    val totalMinuts = duration.toMinutes()

    val dies = totalMinuts / (24 * 60)
    val hores = (totalMinuts % (24 * 60)) / 60
    val minuts = totalMinuts % 60

    return buildString {
        if (dies > 0) append("$dies d, ")
        if (hores > 0 || dies > 0) append("$hores h, ")
        append("$minuts min")
    }
}