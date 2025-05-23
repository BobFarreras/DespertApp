package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.angles

import android.content.Context
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.ChallengeQuestion


object AnglesChallengeGenerator {

    private var loadedQuestions: AnglesQuestionSet? = null

    fun init(context: Context) {
        if (loadedQuestions == null) {
            loadedQuestions = loadAnglesQuestions(context)
        }
    }

    fun generate(model: String): AnglesQuestion {
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
data class AnglesQuestion(
    override val question: String,
    override val options: List<String>,
    override val correctAnswer: String
) : ChallengeQuestion

data class AnglesQuestionSet(
    val basic: List<AnglesQuestion>,
    val advanced: List<AnglesQuestion>,
    val expert: List<AnglesQuestion>
)