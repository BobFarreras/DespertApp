package com.deixebledenkaito.despertapp.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deixebledenkaito.despertapp.domain.model.Usuari
import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository
import com.deixebledenkaito.despertapp.domain.usecase.auth.LoginUseCase
import com.deixebledenkaito.despertapp.domain.usecase.auth.LogoutUseCase
import com.deixebledenkaito.despertapp.domain.usecase.auth.SignupUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.usuari.CrearUsuariUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.usuari.ObtenirUsuariUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val obtenirUsuariUseCase: ObtenirUsuariUseCase,
    private val crearUsuariUseCase: CrearUsuariUseCase,
    private val authRepository: AuthRepository

    ) : ViewModel() {
    private val TAG = "AuthViewModel"

    sealed class AuthEvent {
        data class Login(val email: String, val password: String) : AuthEvent()
        data class Signup(val email: String, val password: String, val name: String) : AuthEvent()
        data class ShowError(val message: String) : AuthEvent()
        data object DismissError : AuthEvent()
        data object Logout : AuthEvent()
        data object CheckAuth : AuthEvent() // Nou event per comprovar autenticació
    }

    data class AuthState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val isAuthenticated: Boolean = false,
        val user: Usuari? = null, // Afegim l'usuari a l'estat
        val authChecked: Boolean = false, // Nou camp per indicar si hem comprovat l'estat,

    )

    // Canviem a StateFlow per a una millor integració amb Compose
    // Inicialitzem el StateFlow amb un valor per defecte
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        checkAuthState()
    }

    fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login(event.email, event.password)
            is AuthEvent.Signup -> signup(event.email, event.password, event.name)
            is AuthEvent.ShowError -> showError(event.message)
            AuthEvent.DismissError -> dismissError()
            AuthEvent.Logout -> logout()
            AuthEvent.CheckAuth -> checkAuthState()
        }
    }

    private fun login(email: String, password: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            loginUseCase(email, password).fold(
                onSuccess = { user ->
                    // Obtenir dades completes de Firestore
                    obtenirUsuariUseCase(user.uid).fold(
                        onSuccess = { empresa ->
                            _state.update {
                                it.copy(
                                    isAuthenticated = true,
                                    isLoading = false,
                                    authChecked = true
                                )
                            }
                        },
                        onFailure = { exception ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Error en obtenir dades: ${exception.message}"
                                )
                            }
                        }
                    )
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error en l'inici de sessió"
                        )
                    }
                }
            )
        }
    }

    private fun signup(email: String, password: String, name: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            signupUseCase(email, password, name).fold(
                onSuccess = { usuari ->
                    // Guardem les dades completes a Firestore
                    crearUsuariUseCase(usuari.copy(
                        nom = name,
                        email = email,

                    )).fold(
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    isAuthenticated = true,
                                    isLoading = false,
                                    authChecked = true
                                )
                            }
                        },
                        onFailure = { exception ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Error en guardar dades: ${exception.message}"
                                )
                            }
                            // Fem logout si no s'han pogut guardar les dades
                            logoutUseCase()
                        }
                    )
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error en el registre"
                        )
                    }
                }
            )
        }
    }


    private fun logout() {
        viewModelScope.launch {
            try {
                // Primer actualitzem l'estat
                _state.update {
                    AuthState(
                        isAuthenticated = false,
                        authChecked = true,
                        error = null,

                        )
                }

                // Després fem el logout (així evitem que es vegi el botó)
                logoutUseCase()

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error en tancar sessió: ${e.message}"
                    )
                }
            }
        }
    }

    private fun showError(message: String) {
        _state.value = _state.value.copy(error = message)
    }

    private fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun checkAuthState() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val firebaseUser = authRepository.getCurrentUser()
            if (firebaseUser != null) {
                obtenirUsuariUseCase(firebaseUser.uid).fold(
                    onSuccess = { usuari ->
                        _state.update {
                            it.copy(
                                isAuthenticated = true,
                                isLoading = false,
                                authChecked = true,
                                user = usuari
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                authChecked = true,
                                error = "Error en obtenir dades d'usuari"
                            )
                        }
                        // Forçar logout si no es poden obtenir les dades
                        logoutUseCase()
                    }
                )
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        authChecked = true,
                        isAuthenticated = false
                    )
                }
            }
        }
    }



}