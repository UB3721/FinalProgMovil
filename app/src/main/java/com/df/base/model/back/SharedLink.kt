package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class SharedLink (
    val sharedLinkId: Int,
    @Serializable
    val sender: User,
    @Serializable
    val recipient: User,
    @Serializable
    val manga: MangaBack,
    val link_: String,
    val altLink: String
)
