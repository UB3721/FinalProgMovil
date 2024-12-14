package com.df.base.data

import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
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

    override suspend fun saveSharedLink(sharedLink: SharedLink): Response<SuccessResponse> = apiService.saveSharedLink(sharedLink)

}