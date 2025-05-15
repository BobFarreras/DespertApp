package com.deixebledenkaito.despertapp.domain.usecase.firestore.usuari

import com.deixebledenkaito.despertapp.domain.model.Usuari
import com.deixebledenkaito.despertapp.domain.repository.firestore.UsuariRepository
import javax.inject.Inject

class CrearUsuariUseCase @Inject constructor(
    private val repository: UsuariRepository
) {
    suspend operator fun invoke(usuari: Usuari): Result<Unit> {
        return repository.crearUsuari(usuari)
    }
}