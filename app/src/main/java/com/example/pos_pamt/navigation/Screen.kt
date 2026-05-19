package com.example.pos_pamt.navigation

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Dashboard : Screen("dashboard")
    object Barang   : Screen("barang")
    object Kas      : Screen("kas")
    object Pelanggan   : Screen("pelanggan")
}
