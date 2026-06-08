package com.example.pos_pamt.data

import android.net.http.HttpResponseCache.install
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://lmjyepvcgeaxdagppnpp.supabase.co",
        supabaseKey = "sb_publishable_mt3XrmnCQ76FQmWRsXQP9g_bTqoR6aH"
    ) {
        install(Auth)
        install(Postgrest)
    }
}

