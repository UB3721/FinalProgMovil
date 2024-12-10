package com.df.base.model.back

import kotlinx.serialization.Serializable

@Serializable
data class AltTitle(
    val altTitleId: Int,
    val mangaId: Int,
    val altTitleDesc: String
)
