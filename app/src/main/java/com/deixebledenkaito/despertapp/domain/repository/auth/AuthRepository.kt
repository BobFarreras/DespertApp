package com.deixebledenkaito.despertapp.domain.repository.auth

import com.deixebledenkaito.despertapp.domain.model.Usuari
import com.google.firebase.auth.FirebaseUser

//domain/repository/AuthRepository
interface AuthRepository {
    suspend fun loginEmpresa(email: String, password: String): Result<FirebaseUser>
    suspend fun signupEmpresa(email: String, password: String, name: String): Result<Usuari>
    suspend fun logout()
    fun getCurrentUser(): FirebaseUser?

}