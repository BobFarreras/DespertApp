package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.angles

import android.content.Context
import com.deixebledenkaito.despertapp.R

import com.google.gson.Gson

fun loadAnglesQuestions (context: Context): AnglesQuestionSet {
    val inputStream = context.resources.openRawResource(R.raw.preguntes_angles)
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, AnglesQuestionSet::class.java)
}
