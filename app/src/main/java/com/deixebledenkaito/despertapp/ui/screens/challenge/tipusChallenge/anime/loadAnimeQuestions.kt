package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.anime

import android.content.Context
import com.deixebledenkaito.despertapp.R
import com.google.gson.Gson

fun loadAnimeQuestions (context: Context): AnimeQuestionSet {
    val inputStream = context.resources.openRawResource(R.raw.preguntes_anime)
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, AnimeQuestionSet::class.java)
}