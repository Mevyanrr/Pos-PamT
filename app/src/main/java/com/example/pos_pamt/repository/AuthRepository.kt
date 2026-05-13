package com.example.pos_pamt.repository

import com.pos.pamt.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow

class AuthRepository {
    private val supabase = SupabaseClientProvider.client
    val sessionStatus: Flow<SessionStatus> = supabase.auth.sessionStatus

    suspend fun login(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {
        supabase.auth.signOut()
    }

    suspend fun isLoggedIn(): Boolean {
        try {
            supabase.auth.awaitInitialization()
        } catch (e: Exception) {
        }
        return supabase.auth.currentSessionOrNull() != null
    }
}
