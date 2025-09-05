package com.example.qryptalk.models

data class Message(
    val id: String,
    val senderId: String,
    val content: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
