package com.df.base.network

import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

private const val BASE_URL =
    "http://192.168.0.13:5000"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface BackApiService {
    @POST("userManga")
    suspend fun saveUserMangaWithManga(
        @Body request: UserManga
    ): Response<SuccessResponse>

    @PUT("userManga")
    suspend fun updateUserManga(
        @Body request: UserManga
    ): Response<SuccessResponse>

    @GET("userManga")
    suspend fun getUserManga(
        @Query("userId") userId: Int
    ): List<UserManga>

    @GET("userManga/favorites")
    suspend fun getFavoritesUserManga(
        @Query("userId") userId: Int
    ): List<UserManga>

    @GET("user")
    suspend fun getAllUsers(
        @Query("userId") userId: Int
    ): List<User>

    @POST("sharedLink")
    suspend fun saveSharedLink(
        @Body request: SharedLink
    ): Response<SuccessResponse>
}

object BackApi {
    val retrofitService: BackApiService by lazy {
        retrofit.create(BackApiService::class.java)
    }
}

@Serializable
data class SuccessResponse(
    val message: String
)