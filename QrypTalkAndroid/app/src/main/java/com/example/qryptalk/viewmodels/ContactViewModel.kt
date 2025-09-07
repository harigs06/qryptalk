package com.example.qryptalk.viewmodels

import androidx.lifecycle.ViewModel
import com.example.qryptalk.models.User
import com.example.qryptalk.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//class ContactViewModel(
//    private val userRepository: UserRepository
//) : ViewModel() {
//
//    private val _users = MutableStateFlow<List<User>>(emptyList())
//    val users: StateFlow<List<User>> = _users
//
//    init {
//        loadUsers()
//    }
//
//    private fun loadUsers() {
//        _users.value = userRepository.getUsers()
//    }
//}
