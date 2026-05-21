package com.efecanseymen.b1.data.model

data class CreateUserRequest(
    val user_name: String,
    val password: String,
    val role: String = "student",
    val user_id: String? = null,
    val email: String? = null
)
