package com.df.base.data

import com.df.base.model.back.Collection
import com.df.base.model.back.LoginRequest
import com.df.base.model.back.MangaCollection
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
    override suspend fun login(loginRequest: LoginRequest) : Response<User> {
        return apiService.login(loginRequest)
    }

    override suspend fun logout(): Response<Unit> = apiService.logout()

    override suspend fun saveUserManga(userManga: UserManga): Response<SuccessResponse> {
        return apiService.saveUserMangaWithManga(userManga)
    }

    override suspend fun updateUserManga(userManga: UserManga): Response<SuccessResponse> {
        return apiService.updateUserManga(userManga)
    }

    override suspend fun getUserByUsername(username: String): User = apiService.getUserByUsername(username)

    override suspend  fun getUserMangaStream(id: Int): List<UserManga> = apiService.getUserManga(id)

    override suspend fun getUserMangaById(userId: Int, mangaId: Int): UserManga = apiService.getUserMangaById(userId, mangaId)

    override suspend  fun getFavoritesUserMangaStream(id: Int): List<UserManga> = apiService.getFavoritesUserManga(id)

    override suspend fun getAllUsers(id: Int): List<User> = apiService.getAllUsers(id)

    override suspend fun getUserData(id: Int): User = apiService.getUserData(id)

    override suspend fun saveSharedLink(sharedLink: SharedLink): Response<SuccessResponse> = apiService.saveSharedLink(sharedLink)

    override suspend fun getAllSharedLink(id: Int): List<SharedLink> = apiService.getAllSharedLink(id)

    override suspend fun getUserStatistics(id: Int): UserStatistics = apiService.getUserStatistics(id)

    override suspend fun saveMangaCollection(mangaCollection: MangaCollection): Response<SuccessResponse> = apiService.saveMangaCollection(mangaCollection)

    override suspend fun getAllCollection(id: Int): List<Collection> = apiService.getAllCollection(id)

    override suspend fun saveCollection(collection: Collection): Response<SuccessResponse> = apiService.saveCollection(collection)

    override suspend fun updateCollectionName(collection: Collection): Response<SuccessResponse> = apiService.updateCollectionName(collection)

    override suspend fun updateSharedLinkState(sharedLink: SharedLink): Response<SuccessResponse> = apiService.updateSharedLinkState(sharedLink)

    override suspend fun getMangaCollection(userId: Int, collectionId: Int): List<MangaCollection> = apiService.getMangaCollection(userId, collectionId)

    override suspend fun deleteCollection(userId: Int, collectionId: Int): Response<SuccessResponse> = apiService.deleteCollection(userId, collectionId)

    override suspend fun deleteMangaCollection(userId: Int, collectionId: Int, mangaId: Int): Response<SuccessResponse> = apiService.deleteMangaCollection(userId, collectionId, mangaId)

    override suspend fun deleteUserManga(userId: Int, mangaId: Int): Response<SuccessResponse> = apiService.deleteUserManga(userId, mangaId)
}