package com.df.base.model.mangadex
import kotlinx.serialization.Serializable

@Serializable
data class Manga(
    val id: String,
    val type: String,
    val attributes: MangaAttributes,
    val relationships: List<TagRelationships>,
)