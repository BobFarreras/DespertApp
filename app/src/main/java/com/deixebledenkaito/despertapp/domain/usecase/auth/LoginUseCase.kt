package com.deixebledenkaito.despertapp.domain.usecase.auth

import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authrepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> {
        return authrepository.loginEmpresa(email, password)
    }
}