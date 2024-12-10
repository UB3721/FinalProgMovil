package com.df.base.model.back

import android.os.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Parcelize
@Serializable
data class UserManga(
    val userId: Int,
    val mangaId: Int?,
    val link: String,
    val altLink: String,
    val userTitle: String,
    val currentChapter: Float,
    val readingStatus: String,
    @Serializable(with = KtxLocalDateSerializer::class)
    val dateAdded: @RawValue LocalDate?,
    val isFavorite: Boolean,
    val userRating: Float,
    val notes: String,
    val mangadexId: String?,
    val coverUrl: String?,
    @Serializable(with = KtxLocalDateSerializer::class)
    val publicationDate: @RawValue LocalDate?,
    val synopsis: String?
) : Parcelable

fun UserManga.toJson(): String = Json.encodeToString(this)

object KtxLocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}