package com.example.qryptalk.auth



import com.example.qryptalk.models.SignUpData
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from


class AuthRepository(private val supabase: SupabaseClient) {

    suspend fun signUpUser(user: SignUpData): Boolean {
        return try {
            supabase.from("SignUpData").insert(user)
            true
        } catch (e: Exception) {
            println("Error signing up: ${e.message}")
            false
        }
    }

    suspend fun loginUser(username: String, password: String): SignUpData? {
        return try {
            val result = supabase
                .from("SignUpData")
                .select()
                .decodeList<SignUpData>()
                .firstOrNull { it.username == username && it.password == password }

            if (result != null) println("Login passed") else println("Login failed")
            result
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            null
        }
    }



}
