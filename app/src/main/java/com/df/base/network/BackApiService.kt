package com.df.base.network

import androidx.room.Delete
import com.df.base.model.back.Collection
import com.df.base.model.back.LoginRequest
import com.df.base.model.back.MangaCollection
import com.df.base.model.back.MangaCollectionRequest
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
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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
    ): Response<List<UserManga>>

    @GET("userManga")
    suspend fun getUserMangaById(
        @Query("userId") userId: Int,
        @Query("mangaId") mangaId: Int
    ): Response<UserManga>

    @GET("userManga/favorites")
    suspend fun getFavoritesUserManga(
        @Query("userId") userId: Int
    ): Response<List<UserManga>>

    @GET("allUsers")
    suspend fun getAllUsers(
        @Query("userId") userId: Int
    ): Response<List<User>>

    @GET("user")
    suspend fun getUserData(
        @Query("userId") userId: Int
    ): User

    @GET("user/{username}")
    suspend fun getUserByUsername(
        @Path("userName") userName: String
    ): User

    @GET("user/statistics")
    suspend fun getUserStatistics(
        @Query("userId") userId: Int
    ): Response<UserStatistics>

    @POST("sharedLink")
    suspend fun saveSharedLink(
        @Body request: SharedLink
    ): Response<SuccessResponse>

    @GET("sharedLink")
    suspend fun getAllSharedLink(
        @Query("userId") userId: Int
    ): Response<List<SharedLink>>

    @PUT("sharedLink")
    suspend fun updateSharedLinkState(
        @Body request: SharedLink
    ): Response<SuccessResponse>

    @POST("mangaCollection")
    suspend fun saveMangaCollection(
        @Body request: MangaCollectionRequest
    ): Response<SuccessResponse>

    @DELETE("mangaCollection")
    suspend fun deleteMangaCollection(
        @Query("userId") userId: Int,
        @Query("collectionId") collectionId: Int,
        @Query("mangaId") mangaId: Int
    ): Response<SuccessResponse>

    @GET("mangaCollection")
    suspend fun getMangaCollection(
        @Query("userId") userId: Int,
        @Query("collectionId") collectionId: Int
    ): Response<List<MangaCollection>>

    @GET("collection/{mangaId}/{userId}")
    suspend fun getCollectionByMangaId(
        @Path("mangaId") mangaId: Int,
        @Path("userId") userId: Int
    ): Response<List<Collection>>

    @GET("collection")
    suspend fun getAllCollection(
        @Query("userId") userId: Int
    ): Response<List<Collection>>

    @POST("collection")
    suspend fun saveCollection(
        @Body request: Collection
    ): Response<SuccessResponse>

    @PUT("collection")
    suspend fun updateCollectionName(
        @Body request: Collection
    ): Response<SuccessResponse>

    @DELETE("userManga")
    suspend fun deleteUserManga(
        @Query("userId") userId: Int,
        @Query("mangaId") mangaId: Int
    ): Response<SuccessResponse>

    @DELETE("collection")
    suspend fun deleteCollection(
        @Query("userId") userId: Int,
        @Query("collectionId") collectionId: Int
    ): Response<SuccessResponse>

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    @POST("users/signup")
    suspend fun signup(@Body loginRequest: LoginRequest): Response<User>
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