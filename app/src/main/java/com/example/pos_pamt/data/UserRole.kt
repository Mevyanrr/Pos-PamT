package com.example.pos_pamt.data

sealed class UserRole {
    object Admin : UserRole()
    object Kasir : UserRole()
}

data class UserSession(
    val email : String   = "",
    val role  : UserRole = UserRole.Kasir,
    val nama  : String   = ""
)
