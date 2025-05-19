package com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge

interface ChallengeQuestion {
    val question: String
    val options: List<String>
    val correctAnswer: String
}