package com.example.qryptalk.models

data class Session(
    val id: String,
    val user1: User,
    val user2: User,
    val key: String,
    val isActive: Boolean
)
