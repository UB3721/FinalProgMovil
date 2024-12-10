package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class SharedLink(
    val sharedLinkId: Int,
    val senderId: Int,
    val recipientId: Int,
    val title: String,
    val coverUrl: String,
    val link_: String,
    val altLink: String
)
