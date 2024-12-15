package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class SharedLink (
    val sharedLinkId: Int,
    val senderId: Int,
    val recipientId: Int,
    @Serializable
    val manga: MangaBack,
    val link_: String,
    val altLink: String
)
