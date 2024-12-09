package com.df.base.model.mangadex
import kotlinx.serialization.Serializable
@Serializable
data class MangaAttributes(
    val title: Map<String, String>?,
    val altTitles: List<Map<String, String>>?,
    val description: Map<String, String>?,
    val isLocked: Boolean?,
    val links: Map<String, String>?,
    val originalLanguage: String,
    val lastVolume: String?,
    val lastChapter: String?,
    val publicationDemographic: String?,
    val status: String?,
    val year: Int?,
    val contentRating: String?,
    val tags: List<Tags>,
    val state: String,
    val chapterNumbersResetOnNewVolume: Boolean?,
    val createdAt: String?,
    val updatedAt: String?,
    val version: Int?,
    val availableTranslatedLanguages: List<String>?,
    val latestUploadedChapter: String?,
)
