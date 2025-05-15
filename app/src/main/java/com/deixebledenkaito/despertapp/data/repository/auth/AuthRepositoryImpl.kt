package com.deixebledenkaito.despertapp.data.repository.auth

import android.util.Log
import com.deixebledenkaito.despertapp.domain.model.Usuari
import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// data/repository/auth/AuthRepositoryImpl.kt
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    private val tag = "AuthRepositoryImpl"

    override suspend fun loginEmpresa(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(tag, "Intentant login per l'usuari amb email: $email")
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("No s'ha pogut obtenir l'usuari")

            Log.d(tag, "Login exitós per l'usuari: ${user.uid}")
            Result.success(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(tag, "Credencials incorrectes", e)
            Result.failure(Exception("Email o contrasenya incorrectes"))
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(tag, "Usuari no trobat", e)
            Result.failure(Exception("No existeix cap empresa amb aquest email"))
        } catch (e: Exception) {
            Log.e(tag, "Error desconegut en login", e)
            Result.failure(Exception("Error en l'inici de sessió: ${e.message}"))
        }
    }

    override suspend fun signupEmpresa(email: String, password: String, name: String): Result<Usuari> {
        return try {
            Log.d(tag, "Intentant registrar nova empresa amb email: $email")

            // 1. Crear l'usuari a Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("No s'ha pogut crear l'usuari")

            // 2. Crear l'objecte Empresa
            val usuari = Usuari(
                id = user.uid,
                nom = name,
                email = email
            )


            Log.d(tag, "Registre exitós per l'usuari: ${usuari.id}")
            Result.success(usuari)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Log.e(tag, "Contrasenya massa feble", e)
            Result.failure(Exception("La contrasenya és massa feble"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e(tag, "Email ja en ús", e)
            Result.failure(Exception("Ja existeix una empresa amb aquest email"))
        } catch (e: Exception) {
            Log.e(tag, "Error desconegut en registre", e)
            Result.failure(Exception("Error en el registre: ${e.message}"))
        }
    }

    override suspend fun logout() {
        try {
            Log.d(tag, "Tancant sessió")
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Log.e(tag, "Error en logout", e)
            throw e
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}