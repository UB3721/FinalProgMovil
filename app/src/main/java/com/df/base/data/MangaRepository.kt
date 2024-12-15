package com.df.base.data

import com.df.base.model.back.Collection
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.model.back.UserStatistics
import com.df.base.network.SuccessResponse
import retrofit2.Response

interface MangasRepository {
    suspend fun saveUserManga(userManga: UserManga): Response<SuccessResponse>

    suspend fun updateUserManga(userManga: UserManga): Response<SuccessResponse>

    suspend fun getUserMangaStream(id: Int): List<UserManga>

    suspend fun getFavoritesUserMangaStream(id: Int): List<UserManga>

    suspend fun getAllUsers(id: Int): List<User>

    suspend fun getUserData(id: Int): User

    suspend fun saveSharedLink(sharedLink: SharedLink): Response<SuccessResponse>

    suspend fun getAllSharedLink(id: Int): List<SharedLink>

    suspend fun getUserStatistics(id: Int): UserStatistics

    suspend fun saveMangaCollection(mangaId: Int, userId: Int, collectionId: Int): Response<SuccessResponse>

    suspend fun deleteMangaCollection(mangaId: Int, userId: Int, collectionId: Int): Response<SuccessResponse>

    suspend fun getAllCollection(id: Int): List<Collection>

    suspend fun saveCollection(mangaId: Int, collectionId: Int, collectionName: String): Response<SuccessResponse>

    suspend fun updateCollectionName(mangaId: Int, collectionId: Int, collectionName: String): Response<SuccessResponse>

    suspend fun updateSharedLinkState(senderId: Int, recipientId: Int, mangaId: Int): Response<SuccessResponse>
}