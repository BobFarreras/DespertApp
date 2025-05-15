package com.deixebledenkaito.despertapp.domain.model

import com.google.firebase.Timestamp

data class Usuari (
    val id: String = "",
    val nom: String = "",
    val email: String = "",
    val data: Timestamp = Timestamp.now(),
)
