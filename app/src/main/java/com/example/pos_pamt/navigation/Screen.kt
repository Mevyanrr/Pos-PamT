package com.example.pos_pamt.navigation

sealed class Screen(val route: String) {
    object Login       : Screen("login")
    object Dashboard   : Screen("dashboard")
    object Produk      : Screen("produk")
    object Kas         : Screen("kas")
    object Pelanggan   : Screen("pelanggan")
    object Pengeluaran : Screen("pengeluaran")
    object Transaksi   : Screen("transaksi")
    object Profil      : Screen("profil")
}
