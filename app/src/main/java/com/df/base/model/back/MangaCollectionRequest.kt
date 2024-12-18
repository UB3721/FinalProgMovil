package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class MangaCollectionRequest (
    val mangaId: Int,
    val userId: Int,
    val collectionIdList: List<Int>
)