package com.deixebledenkaito.despertapp.domain.usecase.auth

import com.deixebledenkaito.despertapp.domain.model.Usuari
import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository
import javax.inject.Inject

// domain/usecase/SignupUseCase.kt
class SignupUseCase @Inject constructor(
    private val authrepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String
    ): Result<Usuari> {
        return authrepository.signupEmpresa(email, password, name)
    }
}