package com.df.base.model.back


import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

