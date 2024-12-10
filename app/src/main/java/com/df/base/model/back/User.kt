package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int,
    val userName: String
)
