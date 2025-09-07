package com.example.qryptalk.repositories

import com.example.qryptalk.data.UserDao
import com.example.qryptalk.data.UserEntity
import com.example.qryptalk.models.Message
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener


import okhttp3.Response

class UserRepository(
    private val userDao: UserDao,
    private val supabaseClient: SupabaseClient
) {
    private var webSocket: WebSocket? = null

    // Local (Room) users
    fun getUsersFromRoom(): Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun addUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    // Remote users (Supabase)
    suspend fun searchUsers(): List<UserEntity> {
        // For prototype, fetch all users from "SignUpData" table
        return supabaseClient
            .from("SignUpData")
            .select()
            .decodeList<UserEntity>()
    }

    // Connect to chat WebSocket
    fun connectWebSocket(
        username: String,
        onMessageReceived: (Message) -> Unit
    ) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("ws://10.0.2.2:8000/ws/$username") // adjust if using real IP or server
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(ws: WebSocket, text: String) {
                val msg = try {
                    Json.decodeFromString<Message>(text)
                } catch (e: Exception) {
                    null
                }
                msg?.let { onMessageReceived(it) }
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                webSocket = null
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                webSocket = null
            }
        })
    }

    fun sendMessage(content: String, senderId: String) {
        val message = Json.encodeToString(
            Message(
                senderId = senderId,
                content = content,
                isFromMe = true
            )
        )
        webSocket?.send(message)
    }

    fun disconnectWebSocket() {
        webSocket?.close(1000, "Session closed")
        webSocket = null
    }
}

