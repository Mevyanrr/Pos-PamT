package com.example.pos_pamt.data

package com.pos.pamt.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://mqikjvrydyqvozyegxmh.supabase.co",
        supabaseKey = "sb_publishable_KfYYE9oHxFCHnM1QO_rbag_iDD4x4PM"
    ) {
        install(Auth)
        install(Postgrest)
    }
}
