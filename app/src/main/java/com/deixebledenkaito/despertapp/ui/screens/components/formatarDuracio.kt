package com.deixebledenkaito.despertapp.ui.screens.components

import java.time.Duration

fun formatarDuracio(duration: Duration): String {
    var totalMinuts = duration.toMinutes()

    if (duration.seconds % 60 > 0) {
        totalMinuts += 1 // arrodonim cap amunt si hi ha segons restants
    }


    val dies = totalMinuts / (24 * 60)
    val hores = (totalMinuts % (24 * 60)) / 60
    val minuts = totalMinuts % 60

    // Si hi ha minuts o hores restants, arrodonim el dia cap amunt
    val diesFinals = if (hores > 0 || minuts > 0) dies + 1 else dies

    return buildString {
        if (dies > 0) append("$dies d, ")
        if (hores > 0 || dies > 0) append("$hores h, ")
        append("$minuts min")
    }
}
