package com.df.base.data

import com.df.base.model.back.Collection
import com.df.base.model.back.LoginRequest
import com.df.base.model.back.MangaCollection
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.model.back.UserStatistics
import com.df.base.network.SuccessResponse
import retrofit2.Response

interface MangasRepository {
    suspend fun login(loginRequest: LoginRequest) : Response<User>
    suspend fun logout(): Response<Unit>

    suspend fun getUserByUsername(username: String): User

    suspend fun saveUserManga(userManga: UserManga): Response<SuccessResponse>

    suspend fun updateUserManga(userManga: UserManga): Response<SuccessResponse>

    suspend fun getUserMangaStream(id: Int): List<UserManga>

    suspend fun getUserMangaById(userId: Int, mangaId: Int): UserManga

    suspend fun getFavoritesUserMangaStream(id: Int): List<UserManga>

    suspend fun getAllUsers(id: Int): List<User>

    suspend fun getUserData(id: Int): User

    suspend fun saveSharedLink(sharedLink: SharedLink): Response<SuccessResponse>

    suspend fun getAllSharedLink(id: Int): List<SharedLink>

    suspend fun getUserStatistics(id: Int): UserStatistics

    suspend fun saveMangaCollection(mangaCollection: MangaCollection): Response<SuccessResponse>

    suspend fun deleteMangaCollection(userId: Int, collectionId: Int, mangaId: Int): Response<SuccessResponse>

    suspend fun getMangaCollection(userId: Int, collectionId: Int): List<MangaCollection>

    suspend fun getAllCollection(id: Int): List<Collection>

    suspend fun saveCollection(collection: Collection): Response<SuccessResponse>

    suspend fun updateCollectionName(collection: Collection): Response<SuccessResponse>

    suspend fun updateSharedLinkState(sharedLink: SharedLink): Response<SuccessResponse>

    suspend fun deleteCollection(userId: Int, collectionId: Int): Response<SuccessResponse>

    suspend fun deleteUserManga(userId: Int, mangaId: Int): Response<SuccessResponse>
}