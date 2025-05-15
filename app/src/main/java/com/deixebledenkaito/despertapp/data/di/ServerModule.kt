package com.deixebledenkaito.despertapp.data.di

import com.deixebledenkaito.despertapp.data.repository.auth.AuthRepositoryImpl
import com.deixebledenkaito.despertapp.data.repository.firestore.AlarmsRepositoryImpl
import com.deixebledenkaito.despertapp.data.repository.firestore.UsuariRepositoryImpl
import com.deixebledenkaito.despertapp.domain.repository.auth.AuthRepository
import com.deixebledenkaito.despertapp.domain.repository.firestore.AlarmsRepository
import com.deixebledenkaito.despertapp.domain.repository.firestore.UsuariRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerModule {
    //    LOGICA AUTENTIFICACIÓ
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    // USUARI
    @Provides
    @Singleton
    fun provideUsuariRepository(firestore: FirebaseFirestore): UsuariRepository {
        return UsuariRepositoryImpl(firestore)
    }

//    ALARMES
    @Provides
    @Singleton
    fun provideAlarmsRepository(firestore: FirebaseFirestore): AlarmsRepository{
        return AlarmsRepositoryImpl(firestore)
    }
}