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
import java.text.SimpleDateFormat
import java.util.*

class SessionViewModel : ViewModel() {

    var messages = mutableStateListOf<Message>()
        private set

    var inputText by mutableStateOf("")
        private set

    var isSecure by mutableStateOf(true)
        private set

    fun onInputChange(newText: String) {
        inputText = newText
    }

    fun sendMessage(senderId: String) {
        if (inputText.isNotBlank()) {
            val msg = Message(
                id = UUID.randomUUID().toString(),
                senderId = senderId,
                content = inputText,
                isFromMe = true,
                timestamp = System.currentTimeMillis()
            )
            messages.add(msg)
            inputText = ""
        }
    }

    fun receiveMessage(senderId: String, text: String) {
        val msg = Message(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            content = text,
            isFromMe = false,
            timestamp = System.currentTimeMillis()
        )
        messages.add(msg)
    }

    fun updateSecurity(secure: Boolean) {
        isSecure = secure
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
