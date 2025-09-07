package com.example.qryptalk.viewmodels

import androidx.lifecycle.ViewModel
import com.example.qryptalk.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

import androidx.lifecycle.viewModelScope
import com.example.qryptalk.network.ChatWebSocketManager
import com.example.qryptalk.network.WSEnvelope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiMessage(
    val id: String,
    val senderId: String,
    val content: String,
    val isFromMe: Boolean,
    val timestamp: Long
)

class ChatViewModel(
    private val currentUserId: String,
    private val contactId: String,
    private val wsManager: ChatWebSocketManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<UiMessage>>(emptyList())
    val messages: StateFlow<List<UiMessage>> = _messages.asStateFlow()

    private val _qber = MutableStateFlow(0.0)
    val qber: StateFlow<Double> = _qber.asStateFlow()

    init {
        // start websocket
        wsManager.start(currentUserId)

        // collect incoming envelopes
        viewModelScope.launch {
            wsManager.incoming.collect { env ->
                when (env.type) {
                    "chat" -> {
                        // only process messages intended for this chat (from contactId or to current)
                        val from = env.from ?: ""
                        val to = env.to ?: ""
                        // accept messages where (from==contactId and to==currentUserId) OR echo from me
                        if ((from == contactId && to == currentUserId) || (from == currentUserId && to == contactId)) {
                            val msg = UiMessage(
                                id = UUID.randomUUID().toString(),
                                senderId = from,
                                content = env.content ?: "",
                                isFromMe = (from == currentUserId),
                                timestamp = env.timestamp ?: System.currentTimeMillis()
                            )
                            _messages.value = _messages.value + msg
                        }
                    }
                    "qber" -> {
                        // qber update for either user
                        val target = env.to
                        if (target == currentUserId || target == contactId || env.to == "all") {
                            _qber.value = env.qber ?: 0.0
                        }
                    }
                    "control" -> {
                        if (env.content == "close_session") {
                            // notify UI to close - you can expose an event flow if needed
                        }
                    }
                }
            }
        }
    }

    fun sendMessage(plain: String) {
        viewModelScope.launch {
            // TODO: encrypt the content with your EncryptionHelper (AES/Keystore) -> produce encryptedBase64
            val encrypted = plain // replace with encryptionHelper.encrypt(plain)
            // build envelope
            val envelope = WSEnvelope(
                type = "chat",
                from = currentUserId,
                to = contactId,
                content = encrypted,
                timestamp = System.currentTimeMillis()
            )
            wsManager.sendEnvelopeAsync(envelope)

            // locally append as sent message (optimistic UI)
            val msg = UiMessage(
                id = UUID.randomUUID().toString(),
                senderId = currentUserId,
                content = plain,
                isFromMe = true,
                timestamp = System.currentTimeMillis()
            )
            _messages.value = _messages.value + msg
        }
    }

    fun requestQber(n: Int = 16, withEve: Boolean = false) {
        viewModelScope.launch {
            val envelope = WSEnvelope(
                type = "request_qber",
                from = currentUserId,
                to = contactId,
                content = null,
                timestamp = System.currentTimeMillis()
            )
            // pack extra fields by sending a simple encoded JSON string in `content` or extend WSEnvelope
            wsManager.sendEnvelopeAsync(envelope)
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.stop()
    }
}
