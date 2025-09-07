package com.example.qryptalk.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qryptalk.data.UserEntity
import com.example.qryptalk.data.UserPreferences
import com.example.qryptalk.models.User
import com.example.qryptalk.repositories.UserRepository
import kotlinx.coroutines.launch

// UserListViewModel.kt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn



class UserListViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val userList: StateFlow<List<UserEntity>> =
        repository.getUsersFromRoom().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _searchResults = MutableStateFlow<List<UserEntity>>(emptyList())
    val searchResults: StateFlow<List<UserEntity>> = _searchResults

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchUsers()
        }
    }

    fun addUser(user: UserEntity) {
        viewModelScope.launch { repository.addUser(user) }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch { repository.deleteUser(user) }
    }

    suspend fun logout(context: Context) {
        val userPrefs = UserPreferences(context)
        userPrefs.clearUser()
    }


}



