package com.df.base.model.back

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    val collectionId: Int,
    val userId: Int,
    val collectionName: String,
    val dateCreated: LocalDateTime,
    val dateLastModified: LocalDateTime
)
