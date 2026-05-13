package com.example.pos_pamt.navigation

import com.example.pos_pamt.ui.view.DashboardScreen
import com.example.pos_pamt.ui.view.ListBarangScreen
import com.example.pos_pamt.ui.view.ListKasScreen
import com.example.pos_pamt.ui.view.LoginScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.pamt.viewmodel.BarangViewModel
import com.pos.pamt.viewmodel.KasViewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.pos_pamt.viewmodel.AuthCheckState
import com.example.pos_pamt.viewmodel.AuthUiState
import com.example.pos_pamt.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()
    when (authCheckState.value) {
        is AuthCheckState.Checking -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthCheckState.Authenticated -> {
            MainNavHost(
                authViewModel    = authViewModel,
                startDestination = Screen.Dashboard.route
            )
        }

        is AuthCheckState.NotAuthenticated -> {
            MainNavHost(
                authViewModel    = authViewModel,
                startDestination = Screen.Login.route
            )
        }
    }
}

@Composable
fun MainNavHost(
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val navController = rememberNavController()

    val email    = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState  = authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                email            = email.value,
                password         = password.value,
                uiState          = uiState.value,
                onEmailChange    = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onLoginClick     = { authViewModel.login() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToBarang = {
                    navController.navigate(Screen.Barang.route)
                },
                onNavigateToKas = {
                    navController.navigate(Screen.Kas.route)
                }
            )
        }

        composable(Screen.Barang.route) {
            val barangViewModel: BarangViewModel = viewModel()
            ListBarangScreen(
                viewModel  = barangViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Kas.route) {
            val kasViewModel: KasViewModel = viewModel()
            ListKasScreen(
                viewModel  = kasViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
