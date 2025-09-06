package com.example.qryptalk.repositories

import com.example.qryptalk.auth.SupabaseClientProvider
import com.example.qryptalk.models.SignUpData
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
//
//object sipabaseUser {
//    private val supabase = SupabaseClientProvider.client
//
//    suspend fun signUpUser(data: SignUpData): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = supabase.from("users").insert(
//                    mapOf(
//                        "first_name" to data.firstName,
//                        "last_name" to data.lastName,
//                        "email" to data.email,
//                        "username" to data.username,
//                        "password" to data.password  // ⚠️ hash later
//                    )
//                ).execute()
//
//                response.error == null
//            } catch (e: Exception) {
//                e.printStackTrace()
//                false
//            }
//        }
//    }
//}
