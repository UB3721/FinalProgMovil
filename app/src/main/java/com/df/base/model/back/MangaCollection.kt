package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class MangaCollection(
    val collectionId: Int,
    val userId: Int,
    val mangaId: Int
)
