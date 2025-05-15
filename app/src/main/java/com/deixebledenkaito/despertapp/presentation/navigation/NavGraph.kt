package com.deixebledenkaito.despertapp.presentation.navigation

import android.os.Build
import android.util.Log
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deixebledenkaito.despertapp.presentation.screen.auth.AuthViewModel
import com.deixebledenkaito.despertapp.presentation.screen.auth.login.LoginScreen
import com.deixebledenkaito.despertapp.presentation.screen.auth.signup.SignupScreen
import com.deixebledenkaito.despertapp.presentation.screen.paginaPrincipal.HomeScreen
import com.deixebledenkaito.despertapp.presentation.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()

    // Controlar canvis d'autenticació
    LaunchedEffect(authState.isAuthenticated, authState.authChecked) {
        when {
            !authState.authChecked -> {
                // Encara no hem comprovat l'estat, mostrar splash
                if (navController.currentDestination?.route != "splash") {
                    navController.navigate("splash") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
            authState.isAuthenticated -> {
                // Usuari autenticat, anar a home
                if (navController.currentDestination?.route != "home") {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            else -> {
                // Usuari no autenticat, anar a login
                if (navController.currentDestination?.route != "login") {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.authChecked) {
            if (authState.isAuthenticated) "home" else "login"
        } else {
            "splash"
        }
    ) {
        composable("splash") {
            SplashScreen()
        }
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToSignup = { navController.navigate("signup") },

            )
        }
        composable("signup") {
            SignupScreen(
                viewModel = authViewModel,
                onNavigateToHome = { navController.navigate("home") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    authViewModel.handleEvent(AuthViewModel.AuthEvent.Logout)
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}


