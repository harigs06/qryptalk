package com.example.qryptalk.repositories

import com.example.qryptalk.models.User

class UserRepository {
    private val users = listOf(
        User("1", "Alice", "alice@example.com"),
        User("2", "Bob", "bob@example.com")
    )

    fun getUsers(): List<User> = users
}
