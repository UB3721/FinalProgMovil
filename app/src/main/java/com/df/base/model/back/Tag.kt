package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val tagId: String,
    val tagMangadexId: String?,
    val tagName: String,
    val tagGroup: String
)
