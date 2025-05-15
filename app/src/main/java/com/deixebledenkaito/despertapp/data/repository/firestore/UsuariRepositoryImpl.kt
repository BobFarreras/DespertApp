package com.deixebledenkaito.despertapp.data.repository.firestore

import com.deixebledenkaito.despertapp.domain.Path.COLLECTION_USUARIS
import com.deixebledenkaito.despertapp.domain.model.Usuari
import com.deixebledenkaito.despertapp.domain.repository.firestore.UsuariRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuariRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): UsuariRepository{


    override suspend fun obtenirUsuari(id: String): Result<Usuari> = try {
        val snapshot = firestore.collection(COLLECTION_USUARIS)
            .document(id)
            .get()
            .await()

        if (snapshot.exists()) {
            Result.success(snapshot.toObject(Usuari::class.java)!!)
        } else {
            Result.failure(Exception("Usuari no trobada"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun crearUsuari(usuari: Usuari): Result<Unit> = try {
        firestore.collection(COLLECTION_USUARIS)
            .document(usuari.id)
            .set(usuari)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}