package com.df.base.ui.profile


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.model.back.UserStatistics
import com.df.base.data.MangasRepository
import com.df.base.ui.add.toUserManga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val mangasRepository: MangasRepository): ViewModel() {
    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private fun convertSharedLinksToUserManga(sharedLinks: List<SharedLink>): List<UserManga> {
        return sharedLinks.map { sharedLink ->
            UserManga(
                userId = sharedLink.recipient.userId,
                mangaId = sharedLink.manga.mangaId,
                link = sharedLink.link_,
                altLink = sharedLink.altLink,
                userTitle = sharedLink.manga.title,
                currentChapter = 0f,
                readingStatus = "",
                dateAdded = null,
                isFavorite = false,
                userRating = 0f,
                notes = "",
                mangadexId = sharedLink.manga.mangadexId,
                coverUrl = sharedLink.manga.coverUrl,
                publicationDate = sharedLink.manga.publicationDate,
                synopsis = sharedLink.manga.synopsis
            )
        }
    }

    fun setUserMangaReadingStatus(
        readingStatus: String,
        userManga: UserManga
    ) {
        _profileUiState.value = _profileUiState.value.copy(
            userMangaList = _profileUiState.value.userMangaList.map { manga ->
                if (manga.mangaId == userManga.mangaId) {
                    manga.copy(readingStatus = readingStatus)
                } else {
                    manga
                }
            }
        )
    }

    fun removeSharedLink(sharedLink: SharedLink) {
        viewModelScope.launch {
            try {
                val response = mangasRepository.updateSharedLinkState(
                    sharedLink
                )
                if (response.isSuccessful) {
                    val successMessage = response.body()?.message
                    Log.d("updateSharedLink", "Success: $successMessage")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("updateSharedLink", "Error: $errorBody")
                }
                fetchSharedLinkList()
            } catch (e: Exception) {
                Log.d("updateSharedLink", "Exception: ${e.message}")
            }
        }
    }

    fun setUser(user: User) {
        _profileUiState.value = _profileUiState.value.copy(
            user = user.toUserDetails()
        )
    }

    fun fetchSharedLinkList() {
        viewModelScope.launch {
            try {
                val sharedLinkList = mangasRepository.getAllSharedLink(_profileUiState.value.user.userId)
                _profileUiState.value = _profileUiState.value
                    .copy(
                        userSharedLinkList = sharedLinkList
                    )
                _profileUiState.value = _profileUiState.value
                    .copy(
                        userMangaList =
                            convertSharedLinksToUserManga(_profileUiState.value.userSharedLinkList)
                    )
            } catch (e: Exception) {
                println("Error fetching user shared link list: ${e.message}")
            }
        }
    }

    fun fetchUserStatistics() {
        viewModelScope.launch {
            try {
                val userStats = mangasRepository.getUserStatistics(_profileUiState.value.user.userId)
                _profileUiState.value = _profileUiState.value.copy(userStats = userStats.toUserStats())
            } catch (e: Exception) {
                println("Error fetching user statistics: ${e.message}")
            }
        }
    }
}

data class ProfileUiState (
    val user: UserDetails = UserDetails(),
    val userStats: UserStats = UserStats(),
    val userSharedLinkList: List<SharedLink> = listOf(),
    val userMangaList: List<UserManga> = listOf()
)

data class UserDetails(
    val userId: Int = 0,
    val userName: String = ""
)

data class UserStats(
    val completed: Int = 0,
    val dropped: Int = 0,
    val onHold: Int = 0,
    val reading: Int = 0
)

fun UserStatistics.toUserStats(): UserStats = UserStats(
    completed = completed,
    dropped = dropped,
    onHold = onHold,
    reading = reading
)

fun User.toUserDetails(): UserDetails = UserDetails(
    userId = userId,
    userName = userName
)

fun UserDetails.toUser(): User = User(
    userId = userId,
    userName = userName
)