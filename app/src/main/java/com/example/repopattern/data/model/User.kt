package com.example.repopattern.data.model

data class User(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String
) {
    val fullName: String get() = "$firstName $lastName"
}
