package com.df.base.model.back
import kotlinx.serialization.Serializable;

@Serializable
data class UserStatistics (
    val completed: Int,
    val dropped: Int,
    val onHold: Int,
    val reading: Int
)