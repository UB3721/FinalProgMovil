package com.df.base.model.back

import android.os.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MangaBack(
    val mangaId: Int,
    val mangadexId: String?,
    val title: String,
    val coverUrl: String,
    @Serializable(with = KtxLocalDateSerializer::class)
    val publicationDate: @RawValue LocalDate?,
    val synopsis: String
): Parcelable
