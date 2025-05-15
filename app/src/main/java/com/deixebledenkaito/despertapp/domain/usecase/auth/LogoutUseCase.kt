package com.deixebledenkaito.despertapp.domain.usecase.auth

import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authrepository: AuthRepository
) {
    suspend operator fun invoke() {
        authrepository.logout()
    }
}