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
            _profileUiState.value = _profileUiState.value.copy(state = ProfileUiState.State.Loading)
            try {
                val response = mangasRepository.updateSharedLinkState(sharedLink)
                if (response.isSuccessful) {
                    val successMessage = response.body()?.message
                    Log.d("updateSharedLink", "Success: $successMessage")
                    _profileUiState.value = _profileUiState.value.copy(state = ProfileUiState.State.Success)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("updateSharedLink", "Error: $errorBody")
                    _profileUiState.value = _profileUiState.value.copy(
                        state = ProfileUiState.State.Error(message = errorBody ?: "Unknown error")
                    )
                }
                fetchSharedLinkList()
            } catch (e: Exception) {
                Log.d("updateSharedLink", "Exception: ${e.message}")
                _profileUiState.value = _profileUiState.value.copy(
                    state = ProfileUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
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
            _profileUiState.value = _profileUiState.value.copy(state = ProfileUiState.State.Loading)

            try {
                val response = mangasRepository.getAllSharedLink(_profileUiState.value.user.userId)
                if (response.isSuccessful) {
                    val sharedLinkList = response.body() ?: listOf()
                    _profileUiState.value = _profileUiState.value.copy(
                        userSharedLinkList = sharedLinkList,
                        state = ProfileUiState.State.Success
                    )
                    _profileUiState.value = _profileUiState.value.copy(
                        userMangaList = convertSharedLinksToUserManga(sharedLinkList)
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("fetchSharedLinkList", "Error: $errorBody")
                    _profileUiState.value = _profileUiState.value.copy(
                        state = ProfileUiState.State.Error(message = errorBody ?: "Unknown error")
                    )
                }
            } catch (e: Exception) {
                Log.d("fetchSharedLinkList", "Exception: ${e.message}")
                _profileUiState.value = _profileUiState.value.copy(
                    state = ProfileUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

    fun fetchUserStatistics() {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(state = ProfileUiState.State.Loading)

            try {
                val response = mangasRepository.getUserStatistics(_profileUiState.value.user.userId)
                if (response.isSuccessful) {
                    val userStats = response.body()?.toUserStats() ?: UserStats()
                    _profileUiState.value = _profileUiState.value.copy(
                        userStats = userStats,
                        state = ProfileUiState.State.Success
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("fetchUserStatistics", "Error: $errorBody")
                    _profileUiState.value = _profileUiState.value.copy(
                        state = ProfileUiState.State.Error(message = errorBody ?: "Unknown error")
                    )
                }
            } catch (e: Exception) {
                Log.d("fetchUserStatistics", "Exception: ${e.message}")
                _profileUiState.value = _profileUiState.value.copy(
                    state = ProfileUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

}

data class ProfileUiState (
    val state: State = State.Idle,
    val user: UserDetails = UserDetails(),
    val userStats: UserStats = UserStats(),
    val userSharedLinkList: List<SharedLink> = listOf(),
    val userMangaList: List<UserManga> = listOf()
){
    sealed class State {
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
        object Idle : State()
    }
}

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