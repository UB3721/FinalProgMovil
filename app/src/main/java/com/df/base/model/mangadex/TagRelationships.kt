package com.df.base.model.mangadex

import kotlinx.serialization.Serializable

@Serializable
data class TagRelationships(
    val id: String,
    val type: String,
    val related: String? = null,
    val attributes: CoverArtAttributes? = null
)
@Serializable
data class CoverArtAttributes(
    val description: String? = null,
    val volume: String? = null,
    val fileName: String? = null,
    val locale: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val version: Int? = null
)