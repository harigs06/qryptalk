package com.example.qryptalk.models

import com.example.qryptalk.R

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePicUrl : Int = R.drawable.baseline_person_24
)
