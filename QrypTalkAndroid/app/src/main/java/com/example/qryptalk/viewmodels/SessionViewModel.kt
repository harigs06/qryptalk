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
import com.example.qryptalk.network.ChatWebSocketManager
import com.example.qryptalk.network.WSEnvelope
import kotlinx.coroutines.launch




class SessionViewModel(
    private val currentUserId: String,
    private val contactId: String,
    private val wsManager: ChatWebSocketManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    val lastQber: Double
        get() = _messages.value.lastOrNull()?.qber ?: 0.0

    init {
        wsManager.start(currentUserId)

        viewModelScope.launch {
            wsManager.incoming.collect { env ->
                when (env.type) {
                    "chat" -> {
                        val from = env.from ?: return@collect
                        val to = env.to ?: return@collect
                        val isForThisChat =
                            (from == contactId && to == currentUserId) ||
                                    (from == currentUserId && to == contactId)

                        if (isForThisChat) {
                            val msg = Message(
                                id = UUID.randomUUID().toString(),
                                senderId = from,
                                content = env.content ?: "",
                                isFromMe = (from == currentUserId),
                                timestamp = env.timestamp ?: System.currentTimeMillis(),
                                qber = env.qber ?: 0.0
                            )
                            _messages.value = _messages.value + msg
                        }
                    }
                    "qber" -> {
                        val systemMsg = Message(
                            id = UUID.randomUUID().toString(),
                            senderId = "server",
                            content = "QBER update: ${env.qber ?: 0.0}",
                            isFromMe = false,
                            timestamp = env.timestamp ?: System.currentTimeMillis(),
                            qber = env.qber ?: 0.0
                        )
                        _messages.value = _messages.value + systemMsg
                    }
                }
            }
        }
    }

    fun sendMessage(plainText: String) {
        viewModelScope.launch {
            val envelope = WSEnvelope(
                type = "chat",
                from = currentUserId,
                to = contactId,
                content = plainText,
                timestamp = System.currentTimeMillis()
            )
            wsManager.sendEnvelopeAsync(envelope)

            val local = Message(
                id = UUID.randomUUID().toString(),
                senderId = currentUserId,
                content = plainText,
                isFromMe = true,
                timestamp = System.currentTimeMillis(),
                qber = 0.0
            )
            _messages.value = _messages.value + local
        }
    }

    fun requestQber() {
        viewModelScope.launch {
            val payload = WSEnvelope(
                type = "request_qber",
                from = currentUserId,
                to = contactId,
                timestamp = System.currentTimeMillis()
            )
            wsManager.sendEnvelopeAsync(payload)
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.stop()
    }
}


