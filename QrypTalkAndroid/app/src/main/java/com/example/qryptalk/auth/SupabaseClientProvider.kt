package com.example.qryptalk.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

//
//



object SupabaseClientProvider {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://wcsgzsqvhatlallrdegk.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Indjc2d6c3F2aGF0bGFsbHJkZWdrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTcxNDQyMjYsImV4cCI6MjA3MjcyMDIyNn0.JLZ7DNkbyOUFjOXk8K5eULSO-CIRh9a0fYrFxff4V2I"
    ) {
        install(Postgrest)
    }
}
