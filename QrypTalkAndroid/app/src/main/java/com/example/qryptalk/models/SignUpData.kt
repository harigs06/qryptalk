package com.example.qryptalk.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SignUpData(
    val id : String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String
)