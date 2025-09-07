package com.example.qryptalk.utils

import com.example.qryptalk.viewmodels.SessionViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qryptalk.network.ChatWebSocketManager

class SessionViewModelFactory(
    private val currentUserId: String,
    private val contactId: String,
    private val wsManager: ChatWebSocketManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(currentUserId, contactId, wsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
