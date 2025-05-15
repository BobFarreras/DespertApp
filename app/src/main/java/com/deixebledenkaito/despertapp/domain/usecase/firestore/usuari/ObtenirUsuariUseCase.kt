package com.deixebledenkaito.despertapp.domain.usecase.firestore.usuari

import com.deixebledenkaito.despertapp.domain.model.Usuari
import com.deixebledenkaito.despertapp.domain.repository.firestore.UsuariRepository
import javax.inject.Inject

class ObtenirUsuariUseCase @Inject constructor(
    private val repository: UsuariRepository
) {
    suspend operator fun invoke(id: String): Result<Usuari> {
        return repository.obtenirUsuari(id)
    }
}