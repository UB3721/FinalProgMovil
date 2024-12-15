package com.df.base.model.back

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MangaBack(
    val mangaId: Int,
    val mangadexId: String?,
    val title: String,
    val coverUrl: String,
    val publicationDate: LocalDate?,
    val synopsis: String
)