package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class MangaTag(
    val mangaId: Int,
    val tagId: Int
)
