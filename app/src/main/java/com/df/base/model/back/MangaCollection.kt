package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class MangaCollection(
    val collection: Collection,
    val user: User,
    val manga: MangaBack
)
