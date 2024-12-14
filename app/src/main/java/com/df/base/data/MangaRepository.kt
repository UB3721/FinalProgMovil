package com.df.base.data
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.network.SuccessResponse
import retrofit2.Response
interface MangasRepository {
    suspend fun saveUserManga(userManga: UserManga): Response<SuccessResponse>

    suspend fun updateUserManga(userManga: UserManga): Response<SuccessResponse>

    suspend fun getUserMangaStream(id: Int): List<UserManga>

    suspend fun getFavoritesUserMangaStream(id: Int): List<UserManga>

    suspend fun getAllUsers(id: Int): List<User>

    suspend fun saveSharedLink(sharedLink: SharedLink): Response<SuccessResponse>
}