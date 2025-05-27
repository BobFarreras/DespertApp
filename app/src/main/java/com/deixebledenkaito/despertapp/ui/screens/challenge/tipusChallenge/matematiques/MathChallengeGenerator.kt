package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.matematiques

import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.ChallengeQuestion

// MathChallengeGenerator.kt
object MathChallengeGenerator {

    fun generate(model: String): MathQuestion {
        return when (model.lowercase()) {
            "avançat" -> generateAdvanced()
            "expert" -> generateExpert()
            else -> generateBasic() // "bàsic" o desconegut
        }
    }

    private fun generateBasic(): MathQuestion {
        val a = (1..20).random()
        val b = (1..20).random()
        val result = a + b
        val options = listOf(result, result + 1, result - 1, result + 2).shuffled()
        return MathQuestion("Quant és $a + $b?", options, result)
    }

    private fun generateAdvanced(): MathQuestion {
        val a = (30..100).random()
        val b = (10..50).random()
        val op = listOf("+", "-").random()
        val result = if (op == "+") a + b else a - b
        val options = listOf(result, result + 2, result - 2, result + 5).shuffled()
        return MathQuestion("Quant és $a $op $b?", options, result)
    }

    private fun generateExpert(): MathQuestion {
        val a = (2..12).random()
        val b = (2..12).random()
        val result = a * b
        val options = listOf(result, result + 1, result - 1, result + 3).shuffled()
        return MathQuestion("Quant és $a × $b?", options, result)
    }
}


// Classe per representar una pregunta matemàtica
data class MathQuestion(
    override val question: String,
    val numericOptions: List<Int>,
    val correctNumericAnswer: Int,

) : ChallengeQuestion {
    override val options: List<String> get() = numericOptions.map { it.toString() }
    override val correctAnswer: String get() = correctNumericAnswer.toString()
}