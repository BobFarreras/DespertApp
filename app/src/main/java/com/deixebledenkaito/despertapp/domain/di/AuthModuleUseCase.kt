package com.deixebledenkaito.despertapp.domain.di

import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository
import com.deixebledenkaito.despertapp.domain.repository.firestore.UsuariRepository
import com.deixebledenkaito.despertapp.domain.usecase.auth.LoginUseCase
import com.deixebledenkaito.despertapp.domain.usecase.auth.LogoutUseCase
import com.deixebledenkaito.despertapp.domain.usecase.auth.SignupUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.usuari.CrearUsuariUseCase
import com.deixebledenkaito.despertapp.domain.usecase.firestore.usuari.ObtenirUsuariUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AuthModuleUseCase {

    // 0000000000000000000000000000 MODUL DE L'AUTENTIFICACIO  USUARI 00000000000000000000000
    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }
    @Provides
    fun provideSignupUseCase(repository: AuthRepository): SignupUseCase {
        return SignupUseCase(repository)
    }
    @Provides
    fun provideLogoutUseCase(repository: AuthRepository): LogoutUseCase {
        return LogoutUseCase(repository)
    }

//    =========================== DADES USUARI ======================================
    @Provides
    fun provideCrearUsuariUseCase(repository: UsuariRepository): CrearUsuariUseCase {
        return CrearUsuariUseCase(repository)
    }
    @Provides
    fun provideObtenirUsuariUseCase(repository: UsuariRepository): ObtenirUsuariUseCase {
        return ObtenirUsuariUseCase(repository)
    }

}