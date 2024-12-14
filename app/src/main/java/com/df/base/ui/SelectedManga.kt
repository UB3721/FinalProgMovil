package com.df.base.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Parcelize
@Serializable
data class SelectedManga(
    val id: String,
    val title: String,
    val synopsis: String,
    val coverUrl: String
) : Parcelable

fun SelectedManga.toJson(): String = Json.encodeToString(this)