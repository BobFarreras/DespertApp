package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.anime

import android.content.Context
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.ChallengeQuestion

object AnimeChallengeGenerator{

    private var loadedQuestions: AnimeQuestionSet? = null

    fun init(context: Context) {
        if (loadedQuestions == null) {
            loadedQuestions = loadAnimeQuestions(context)
        }
    }

    fun generate(model: String): AnimeQuestion {
        // Aquesta línia assegura que el tipus sigui 100% concret
        val questions: List<AnimeQuestion> = when (model.lowercase()) {
            "bàsic" -> loadedQuestions?.basic
            "avançat" -> loadedQuestions?.advanced
            "expert" -> loadedQuestions?.expert
            else -> loadedQuestions?.basic
        } ?: emptyList()

        return questions.random()
    }
}
// Classe per representar una pregunta de cultura catalana
data class AnimeQuestion(
    override val question: String,
    override val options: List<String>,
    override val correctAnswer: String
) : ChallengeQuestion

data class AnimeQuestionSet(
    val basic: List<AnimeQuestion>,
    val advanced: List<AnimeQuestion>,
    val expert: List<AnimeQuestion>
)