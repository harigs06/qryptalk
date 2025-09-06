package com.example.qryptalk.repositories

import com.example.qryptalk.models.User

// UserRepository.kt
class UserRepository {
    private val users = listOf(
        User(id = "1", name = "Alice", email = "alice@mail.com"),
        User(id = "2", name = "Bob", email = "bob@mail.com"),
        User(id = "3", name = "Charlie", email = "charlie@mail.com"),
    )

    fun getUsers(): List<User> = users
}
