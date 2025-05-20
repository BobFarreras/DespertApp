package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.cultura

import android.content.Context
import com.deixebledenkaito.despertapp.R
import com.google.gson.Gson

fun loadCulturaQuestions(context: Context): CulturaQuestionSet {
    val inputStream = context.resources.openRawResource(R.raw.preguntes_cultura)
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, CulturaQuestionSet::class.java)
}