package com.df.base.model.back

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    val collectionId: Int,
    val userId: Int,
    val collectionName: String,
    @Serializable(with = KtxLocalDateSerializer::class)
    val dateCreated: @RawValue LocalDate?,
    @Serializable(with = KtxLocalDateSerializer::class)
    val dateLastModified: @RawValue LocalDate?
)
