package com.df.base.model.mangadex

import kotlinx.serialization.Serializable

@Serializable
data class MangaList(
    val result: String,
    val response: String,
    val data: List<Manga>,
    val limit: Int,
    val offset: Int,
    val total: Int
)
