package com.example.pos_pamt.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.pos_pamt.data.UserRole
import com.example.pos_pamt.ui.view.*
import com.example.pos_pamt.viewmodel.*

private val routesWithNavBar = listOf(
    Screen.Dashboard.route,
    Screen.Produk.route,
    Screen.Pelanggan.route,
    Screen.Transaksi.route,
    Screen.Profil.route
)

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val state = authViewModel.authCheckState.collectAsStateWithLifecycle()
    when (state.value) {
        is AuthCheckState.Checking         -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFF00B5A3)) }
        is AuthCheckState.Authenticated    -> MainNavHost(authViewModel, Screen.Dashboard.route)
        is AuthCheckState.NotAuthenticated -> MainNavHost(authViewModel, Screen.Login.route)
    }
}

@Composable
fun MainNavHost(authViewModel: AuthViewModel, startDestination: String) {
    val navController  = rememberNavController()
    val email          = authViewModel.email.collectAsStateWithLifecycle()
    val password       = authViewModel.password.collectAsStateWithLifecycle()
    val uiState        = authViewModel.uiState.collectAsStateWithLifecycle()
    val userSession    = authViewModel.userSession.collectAsStateWithLifecycle()
    val currentRoute   = navController.currentBackStackEntryAsState().value?.destination?.route
    val isAdmin        = userSession.value.role is UserRole.Admin
    val showNavBar     = currentRoute in routesWithNavBar

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
            authViewModel.resetState()
        }
    }

    Scaffold(
        bottomBar = {
            if (showNavBar) {
                BottomNavBar(
                    currentRoute = currentRoute ?: "",
                    isAdmin      = isAdmin,
                    onBeranda    = { navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Dashboard.route) { inclusive = true } } },
                    onTransaksi  = { navController.navigate(Screen.Transaksi.route) },
                    onPelanggan  = { navController.navigate(Screen.Pelanggan.route) },
                    onProduk     = { navController.navigate(Screen.Produk.route) },
                    onProfil     = { navController.navigate(Screen.Profil.route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = startDestination, modifier = Modifier.padding(innerPadding)) {

            composable(Screen.Login.route) {
                LoginScreen(
                    email = email.value, password = password.value, uiState = uiState.value,
                    onEmailChange    = authViewModel::onEmailChange,
                    onPasswordChange = authViewModel::onPasswordChange,
                    onLoginClick     = { authViewModel.login() }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    userSession             = userSession.value,
                    onLogoutClick           = { authViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(Screen.Dashboard.route) { inclusive = true } } },
                    onNavigateToProduk      = { navController.navigate(Screen.Produk.route) },
                    onNavigateToKas         = { navController.navigate(Screen.Kas.route) },
                    onNavigateToPelanggan   = { navController.navigate(Screen.Pelanggan.route) },
                    onNavigateToPengeluaran = { navController.navigate(Screen.Pengeluaran.route) },
                    onNavigateToTransaksi   = { navController.navigate(Screen.Transaksi.route) },
                    onNavigateToProfil      = { navController.navigate(Screen.Profil.route) }
                )
            }

            composable(Screen.Produk.route) {
                val vm: ProdukViewModel = viewModel()
                LaunchedEffect(Unit) { if (isAdmin) vm.loadLogProduk() }
                ProdukScreen(viewModel = vm, isAdmin = isAdmin, onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Kas.route) {
                val vm: KasViewModel = viewModel()
                LaunchedEffect(Unit) { vm.loadLogKas() }
                KasScreen(viewModel = vm, onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Pelanggan.route) {
                val vm: PelangganViewModel = viewModel()
                PelangganScreen(viewModel = vm, isAdmin = isAdmin, onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Pengeluaran.route) {
                val vm: PengeluaranViewModel = viewModel()
                LaunchedEffect(Unit) { vm.loadLogPengeluaran() }
                PengeluaranScreen(viewModel = vm, onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Transaksi.route) {
                val vm: PenjualanViewModel = viewModel()
                TransaksiScreen(
                    viewModel   = vm,
                    isAdmin     = isAdmin,
                    kasirId     = userSession.value.id,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Profil.route) {
                ProfilScreen(
                    userSession   = userSession.value,
                    onLogoutClick = { authViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(Screen.Dashboard.route) { inclusive = true } } },
                    onBackClick   = { navController.popBackStack() }
                )
            }
        }
    }
}