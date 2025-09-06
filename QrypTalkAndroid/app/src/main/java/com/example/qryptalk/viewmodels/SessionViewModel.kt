package com.example.qryptalk.viewmodels

import androidx.lifecycle.ViewModel
import com.example.qryptalk.models.Message
import com.example.qryptalk.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


import androidx.compose.runtime.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.util.*

import androidx.lifecycle.viewModelScope
import com.example.qryptalk.network.ChatWebSocketClient
import kotlinx.coroutines.launch


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SessionViewModel(private val userId: String) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private var webSocketClient: ChatWebSocketClient? = null

    fun connect() {
        webSocketClient = ChatWebSocketClient(userId, ::onMessageReceived).apply {
            connect()
        }
    }

    private fun onMessageReceived(content: String, senderId: String) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            content = content,
            isFromMe = senderId == userId
        )
        viewModelScope.launch {
            _messages.value = _messages.value + message
        }
    }

    fun sendMessage(content: String) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            senderId = userId,
            content = content,
            isFromMe = true
        )
        viewModelScope.launch {
            _messages.value = _messages.value + message
        }
        webSocketClient?.sendMessage(content)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient?.close()
    }
}

