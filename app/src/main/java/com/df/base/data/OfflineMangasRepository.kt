package com.df.base.data

import com.df.base.model.back.Collection
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.model.back.UserStatistics
import com.df.base.network.BackApiService
import com.df.base.network.SuccessResponse
import retrofit2.Response


class OfflineMangasRepository(
    private val apiService: BackApiService
): MangasRepository {
    override suspend fun saveUserManga(userManga: UserManga): Response<SuccessResponse> {
        return apiService.saveUserMangaWithManga(userManga)
    }

    override suspend fun updateUserManga(userManga: UserManga): Response<SuccessResponse> {
        return apiService.updateUserManga(userManga)
    }

    override suspend  fun getUserMangaStream(id: Int): List<UserManga> = apiService.getUserManga(id)

    override suspend  fun getFavoritesUserMangaStream(id: Int): List<UserManga> = apiService.getFavoritesUserManga(id)

    override suspend fun getAllUsers(id: Int): List<User> = apiService.getAllUsers(id)

    override suspend fun getUserData(id: Int): User = apiService.getUserData(id)

    override suspend fun saveSharedLink(sharedLink: SharedLink): Response<SuccessResponse> = apiService.saveSharedLink(sharedLink)

    override suspend fun getAllSharedLink(id: Int): List<SharedLink> = apiService.getAllSharedLink(id)

    override suspend fun getUserStatistics(id: Int): UserStatistics = apiService.getUserStatistics(id)

    override suspend fun saveMangaCollection(mangaId: Int, userId: Int, collectionId: Int): Response<SuccessResponse> = apiService.saveMangaCollection(mangaId, userId, collectionId)

    override suspend fun deleteMangaCollection(mangaId: Int, userId: Int, collectionId: Int): Response<SuccessResponse> = apiService.deleteMangaCollection(mangaId, userId, collectionId)

    override suspend fun getAllCollection(id: Int): List<Collection> = apiService.getAllCollection(id)

    override suspend fun saveCollection(mangaId: Int, collectionId: Int, collectionName: String): Response<SuccessResponse> = apiService.saveCollection(mangaId, collectionId, collectionName)

    override suspend fun updateCollectionName(mangaId: Int, collectionId: Int, collectionName: String): Response<SuccessResponse> = apiService.updateCollectionName(mangaId, collectionId, collectionName)

    override suspend fun updateSharedLinkState(senderId: Int, recipientId: Int, mangaId: Int): Response<SuccessResponse> = apiService.updateSharedLinkState(senderId, recipientId, mangaId)

}