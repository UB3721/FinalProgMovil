package com.df.base.ui.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.data.MangasRepository
import com.df.base.model.back.MangaBack
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DisplayViewModel(private val mangasRepository: MangasRepository): ViewModel() {
    private val _displayUiState = MutableStateFlow(DisplayUiState())
    val displayUiState: StateFlow<DisplayUiState> = _displayUiState.asStateFlow()

    fun updateIsDescExpanded() {
        _displayUiState.value = _displayUiState.value.copy(
            isDescExpanded = !_displayUiState.value.isDescExpanded
        )
    }

    fun updateSelectedUser(user: User, userManga: MangaDetails) {
        _displayUiState.value = _displayUiState.value.copy(
            selectedUser =
            _displayUiState.value.selectedUser.copy(
                userId = user.userId,
                userName = user.userName
            ),
            sharedLink =
            _displayUiState.value.sharedLink.copy(
                manga = MangaBack(
                    mangaId = userManga.mangaId ?: 0,
                    mangadexId = userManga.mangadexId,
                    title = userManga.userTitle,
                    coverUrl = userManga.coverUrl ?: "",
                    publicationDate = userManga.publicationDate,
                    synopsis = userManga.synopsis ?: ""
                ),
                link = userManga.link,
                altLink = userManga.altLink,
                sender = User(userManga.userId, ""),
                recipient = User(userId = user.userId, userName = user.userName)
            )
        )
    }

    fun setUiState() {
        _displayUiState.value = _displayUiState.value.copy(state = DisplayUiState.State.Loading)
    }

    fun setUser(user: User) {
        _displayUiState.value = _displayUiState.value.copy(
            userId = user.userId
        )
    }

    suspend fun fetchAllUsers() {
        _displayUiState.value = _displayUiState.value.copy(state = DisplayUiState.State.Loading)

        try {
            val response = mangasRepository.getAllUsers(_displayUiState.value.userId)

            if (response.isSuccessful) {
                _displayUiState.value = _displayUiState.value.copy(
                    userList = response.body()?: listOf()
                )

            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("getUsers", errorBody)
            }
        } catch (e: Exception) {
            Log.d("getUsers", e.localizedMessage ?: "Unknown error")
        }
    }

    suspend fun saveSharedLink() {
        _displayUiState.value = _displayUiState.value.copy(state = DisplayUiState.State.Loading)

        try {
            val response = mangasRepository.saveSharedLink(displayUiState.value.sharedLink.toSharedLink())

            if (response.isSuccessful) {
                val successMessage = response.body()?.message
                Log.d("saveSharedLink", "Success: $successMessage")
                _displayUiState.value = _displayUiState.value.copy(
                    state = DisplayUiState.State.Success
                )
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("saveSharedLink", "Error: $errorBody")
                _displayUiState.value = _displayUiState.value.copy(
                    state = DisplayUiState.State.Error(message = errorBody ?: "Unknown error")
                )
            }
        } catch (e: Exception) {
            Log.d("saveSharedLink", "Exception: ${e.message}")
            _displayUiState.value = _displayUiState.value.copy(
                state = DisplayUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
            )
        } finally {
            _displayUiState.value = _displayUiState.value.copy(selectedUser = DisplayUser())
        }
    }

}

data class DisplayUiState(
    val state: State = State.Idle,
    val userId: Int = 0,
    val isDescExpanded: Boolean = false,
    val selectedUser: DisplayUser = DisplayUser(),
    val sharedLink: DisplaySharedLink = DisplaySharedLink(),
    val userList: List<User> = listOf()
) {
    sealed class State {
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
        object Idle : State()
    }
}

data class DisplayUser(
    val userId: Int = 0,
    val userName: String = ""
)

data class DisplaySharedLink(
    val sharedLinkId: Int = 0,
    val sender: User = User(1, ""),
    val recipient: User = User(0, ""),
    val manga: MangaBack = MangaBack(
        mangaId =  0,
        mangadexId = "",
        title = "",
        coverUrl = "",
        publicationDate = null,
        synopsis = ""
    ),
    val link: String = "",
    val altLink: String = ""
)

//fun User.toDisplayUser(): DisplayUser = DisplayUser(
//    userId = userId,
//    userName = userName
//)
//
fun DisplayUser.toUser(): User = User(
    userId = userId,
    userName = userName
)
//
//fun SharedLink.toDisplaySharedLink(): DisplaySharedLink = DisplaySharedLink(
//    sharedLinkId = sharedLinkId,
//    senderId = senderId,
//    recipientId = recipientId,
//    title = title,
//    coverUrl = coverUrl,
//    link_ = link,
//    altLink = altLink
//)

fun DisplaySharedLink.toSharedLink(): SharedLink = SharedLink(
    sharedLinkId = sharedLinkId,
    sender = sender,
    recipient = recipient,
    manga = manga,
    link_ = link,
    altLink = altLink
)