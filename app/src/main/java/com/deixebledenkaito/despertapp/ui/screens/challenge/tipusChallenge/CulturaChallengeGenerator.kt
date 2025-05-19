package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge

import android.content.Context

// CulturaChallengeGenerator.kt
object CulturaChallengeGenerator {

    private var loadedQuestions: CulturaQuestionSet? = null

    fun init(context: Context) {
        if (loadedQuestions == null) {
            loadedQuestions = loadCulturaQuestions(context)
        }
    }

    fun generate(model: String): CulturaQuestion {
        val questions = when (model.lowercase()) {
            "bàsic" -> loadedQuestions?.basic
            "avançat" -> loadedQuestions?.advanced
            "expert" -> loadedQuestions?.expert
            else -> loadedQuestions?.basic
        } ?: emptyList()

        return questions.random()
    }
}
// Classe per representar una pregunta de cultura catalana
data class CulturaQuestion(
    override val question: String,
    override val options: List<String>,
    override val correctAnswer: String
) : ChallengeQuestion

data class CulturaQuestionSet(
    val basic: List<CulturaQuestion>,
    val advanced: List<CulturaQuestion>,
    val expert: List<CulturaQuestion>
)