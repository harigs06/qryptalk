package com.example.qryptalk.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

import okio.ByteString


class ChatWebSocketClient(
    private val username: String,
    private val onMessageReceived: (message: String, senderId: String) -> Unit
) {
    private val client = OkHttpClient()

    private val request: Request = Request.Builder()
        .url("ws://10.0.2.2:8000/chat/ws/$username")
        .build()

    private var webSocket: WebSocket? = null

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("✅ Connected to WebSocket as $username")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("📩 Received message: $text")
            // Assuming backend sends messages in "senderId:content" format
            val parts = text.split(":", limit = 2)
            if (parts.size == 2) {
                val senderId = parts[0]
                val content = parts[1]
                onMessageReceived(content, senderId)
            } else {
                onMessageReceived(text, "unknown")
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("📩 Received bytes: ${bytes.hex()}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
            println("Closing WebSocket: $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println(" WebSocket error: ${t.message}")
        }
    }

    fun connect() {
        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Bye")
    }
}

