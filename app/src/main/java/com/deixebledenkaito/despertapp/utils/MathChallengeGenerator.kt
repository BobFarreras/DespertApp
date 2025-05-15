package com.deixebledenkaito.despertapp.utils

// MathChallengeGenerator.kt
object MathChallengeGenerator {
    // Genera una pregunta de suma aleatòria amb 4 opcions
    fun generate(): MathQuestion {
        val a = (1..20).random()
        val b = (1..20).random()
        val result = a + b
        val options = listOf(result, result + 1, result - 1, result + 2).shuffled()
        return MathQuestion("Quant és $a + $b?", options, result)
    }
}

// Classe per representar una pregunta matemàtica
data class MathQuestion(
    val question: String, // Text de la pregunta
    val options: List<Int>, // Opcions múltiples
    val correctAnswer: Int // Resposta correcta
)