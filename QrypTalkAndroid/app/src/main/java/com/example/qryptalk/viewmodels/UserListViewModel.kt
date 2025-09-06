package com.example.qryptalk.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qryptalk.data.UserPreferences
import com.example.qryptalk.models.User
import com.example.qryptalk.repositories.UserRepository
import kotlinx.coroutines.launch

// UserListViewModel.kt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserListViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow(repository.getUsers())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchChange(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _users.value = repository.getUsers().filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true)
            }
        }
    }

    suspend fun logout(context: Context) {
        val userPrefs = UserPreferences(context)
        userPrefs.clearUser()
    }



    fun getUserById(id: String): User? {
        return repository.getUsers().find { it.id == id }
    }
}
