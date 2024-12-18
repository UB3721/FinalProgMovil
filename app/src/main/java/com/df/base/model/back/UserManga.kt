package com.df.base.model.back

import android.os.Parcel
import android.os.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parceler
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
) : Parcelable {

    companion object : Parceler<UserManga> {
        override fun UserManga.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(userId)
            parcel.writeValue(mangaId)
            parcel.writeString(link)
            parcel.writeString(altLink)
            parcel.writeString(userTitle)
            parcel.writeFloat(currentChapter)
            parcel.writeString(readingStatus)

            parcel.writeString(dateAdded?.toString())

            parcel.writeByte(if (isFavorite) 1 else 0)
            parcel.writeFloat(userRating)
            parcel.writeString(notes)
            parcel.writeString(mangadexId)
            parcel.writeString(coverUrl)

            parcel.writeString(publicationDate?.toString())

            parcel.writeString(synopsis)
        }

        override fun create(parcel: Parcel): UserManga {
            val userId = parcel.readInt()
            val mangaId = parcel.readValue(Int::class.java.classLoader) as? Int
            val link = parcel.readString() ?: ""
            val altLink = parcel.readString() ?: ""
            val userTitle = parcel.readString() ?: ""
            val currentChapter = parcel.readFloat()
            val readingStatus = parcel.readString() ?: ""

            val dateAdded = parcel.readString()?.let { LocalDate.parse(it) }

            val isFavorite = parcel.readByte() != 0.toByte()
            val userRating = parcel.readFloat()
            val notes = parcel.readString() ?: ""
            val mangadexId = parcel.readString()
            val coverUrl = parcel.readString()

            val publicationDate = parcel.readString()?.let { LocalDate.parse(it) }
            val synopsis = parcel.readString()

            return UserManga(
                userId,
                mangaId,
                link,
                altLink,
                userTitle,
                currentChapter,
                readingStatus,
                dateAdded,
                isFavorite,
                userRating,
                notes,
                mangadexId,
                coverUrl,
                publicationDate,
                synopsis
            )
        }
    }
}

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