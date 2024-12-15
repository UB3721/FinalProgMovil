package com.df.base.network

import com.df.base.model.mangadex.MangaList
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://api.mangadex.org"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface MangasApiService {
    @GET("/manga")
    suspend fun getMangas(
        @Query("title") title: String,
        @Query("includes[]") includes: String = "cover_art"
    ): MangaList
}

object MangasApi {
   val retrofitService: MangasApiService by lazy {
        retrofit.create(MangasApiService::class.java)
    }
}