package com.example.pos_pamt.repository

import com.example.pos_pamt.data.ProfileRow
import com.example.pos_pamt.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
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
        try { supabase.auth.awaitInitialization() } catch (e: Exception) {}
        return supabase.auth.currentSessionOrNull() != null
    }

    suspend fun getCurrentProfile(): ProfileRow? {
        val uid = supabase.auth.currentSessionOrNull()?.user?.id ?: return null
        return try {
            supabase.postgrest["profiles"]
                .select { filter { eq("id", uid) } }
                .decodeSingleOrNull<ProfileRow>()
        } catch (e: Exception) { null }
    }

    fun getCurrentUserId(): String? {
        return supabase.auth.currentSessionOrNull()?.user?.id
    }
}