package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class ReadingStatus(
    val readingStatusId: Int,
    val description: String
)
