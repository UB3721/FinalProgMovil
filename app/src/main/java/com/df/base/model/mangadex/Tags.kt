package com.df.base.model.mangadex

import kotlinx.serialization.Serializable

@Serializable
data class Tags(
    val id: String,
    val type: String,
    val attributes: TagAttributes,
    val relationships: List<TagRelationships>,
)
