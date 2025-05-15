package com.deixebledenkaito.despertapp.domain.repository.firestore

import com.deixebledenkaito.despertapp.domain.model.Usuari

interface UsuariRepository {
    suspend fun obtenirUsuari(id: String): Result<Usuari>
    suspend fun crearUsuari(usuari: Usuari): Result<Unit>
}