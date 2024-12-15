package com.df.base.network

import com.df.base.model.back.Collection
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.model.back.UserStatistics
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("allUsers")
    suspend fun getAllUsers(
        @Query("userId") userId: Int
    ): List<User>

    @GET("user")
    suspend fun getUserData(
        @Query("userId") userId: Int
    ): User

    @GET("user/statistics")
    suspend fun getUserStatistics(
        @Query("userId") userId: Int
    ): UserStatistics

    @POST("sharedLink")
    suspend fun saveSharedLink(
        @Body request: SharedLink
    ): Response<SuccessResponse>

    @GET("sharedLink")
    suspend fun getAllSharedLink(
        @Query("userId") userId: Int
    ): List<SharedLink>

    @POST("mangaCollection")
    suspend fun saveMangaCollection(
        @Query("mangaId") MangaId: Int,
        @Query("userId") userId: Int,
        @Query("collectionId") collectionId: Int
    ): Response<SuccessResponse>

    @DELETE("mangaCollection")
    suspend fun deleteMangaCollection(
        @Query("mangaId") MangaId: Int,
        @Query("userId") userId: Int,
        @Query("collectionId") collectionId: Int
    ): Response<SuccessResponse>

    @GET("collection")
    suspend fun getAllCollection(
        @Query("userId") userId: Int
    ): List<Collection>

    @POST("collection")
    suspend fun saveCollection(
        @Query("userId") userId: Int,
        @Query("collectionId") collectionId: Int,
        @Query("collectionName") collectionName: String
    ): Response<SuccessResponse>

    @PUT("collection")
    suspend fun updateCollectionName(
        @Query("userId") userId: Int,
        @Query("collectionId") collectionId: Int,
        @Query("collectionName") collectionName: String
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