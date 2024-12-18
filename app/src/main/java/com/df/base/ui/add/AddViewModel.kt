package com.df.base.ui.add

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.df.base.data.MangasRepository
import com.df.base.model.back.Collection
import com.df.base.model.back.MangaBack
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.ui.SelectedManga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddViewModel(private val mangasRepository: MangasRepository): ViewModel() {
    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    fun initializeState(
        readingStatus: List<String>,
        selectedStatus: String,
        selectedManga: SelectedManga
    ) {
        if (_uiState.value.readingStatus.isEmpty()) {
            _uiState.value = _uiState.value.copy(readingStatus = readingStatus)
            _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(readingStatus = selectedStatus))
        }
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(coverUrl = selectedManga.coverUrl))
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(synopsis = selectedManga.synopsis))
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(userTitle = selectedManga.title))
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(mangadexId = selectedManga.id))
    }

    fun updateSiteLink(link: String) {
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(link = link))
    }

    fun updateAltSiteLink(link: String) {
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(altLink = link))
    }

    fun updateCurrentChapter(currentChapter: String) {
        if (currentChapter.toFloatOrNull() != null) {
            _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(currentChapter = currentChapter))
        }
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(notes = notes))
    }

    fun updateRating(rating: String) {
        if (rating.toFloatOrNull() != null ) {
            _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(userRating = rating))
        }
    }

    fun updateSelectedStatus(selectedStatus: String) {
        _uiState.value = _uiState.value.copy(mangaDetails = _uiState.value.mangaDetails.copy(readingStatus = selectedStatus))
    }

    fun updateIsTitleExpanded() {
        _uiState.value = _uiState.value.copy(isTitleExpanded = !uiState.value.isTitleExpanded)
    }

    fun updateIsDropdownExpanded(isDropdownExpanded: Boolean) {
        _uiState.value = _uiState.value.copy(isDropdownExpanded = isDropdownExpanded)
    }

    fun updateIsFavoriteChanged() {
        _uiState.value = _uiState.value.copy(
            mangaDetails = _uiState.value.mangaDetails
                .copy(
                    isFavorite = !_uiState.value.mangaDetails.isFavorite
                )
        )
    }

    fun setUser(user: User) {
        _uiState.value = _uiState.value.copy(
            mangaDetails = _uiState.value.mangaDetails.copy(
                userId = user.userId
            )
        )
    }

    fun setUiState() {
        _uiState.value = _uiState.value.copy(state = AddUiState.State.Loading)
    }

    private fun validateUserManga(mangaDetails: MangaDetails): Result<Unit> {
        if (mangaDetails.link.isBlank() || mangaDetails.altLink.isBlank()) {
            return Result.failure(Exception("Link and AltLink cannot be empty."))
        }

        val currentChapterFloat = mangaDetails.currentChapter.toFloatOrNull()
            ?: return Result.failure(Exception("Current chapter must be a valid float number."))

        val ratingFloat = mangaDetails.userRating.toFloatOrNull()
        if (ratingFloat == null || ratingFloat !in 0f..10f) {
            return Result.failure(Exception("Rating must be a float between 0 and 10."))
        }

        return Result.success(Unit)
    }

    suspend fun saveUserManga() {
        _uiState.value = _uiState.value.copy(state = AddUiState.State.Loading)

        val validationResult = validateUserManga(uiState.value.mangaDetails)

        if (validationResult.isFailure) {
            _uiState.value = _uiState.value.copy(
                state = AddUiState.State.Error(message = validationResult.exceptionOrNull()?.message ?: "Unknown error")
            )
            return
        }

        try {
            val response = mangasRepository.saveUserManga(uiState.value.mangaDetails.toUserManga())

            if (response.isSuccessful) {
                val successMessage = response.body()?.message
                Log.d("SaveUserManga", "Success: $successMessage")

                _uiState.value = _uiState.value.copy(
                    state = AddUiState.State.Success
                )
                Log.d("SaveUserManga", "Updated state: ${_uiState.value.state}")

            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("SaveUserManga", "Error: $errorBody")

                _uiState.value = _uiState.value.copy(
                    state = AddUiState.State.Error(message = errorBody)
                )
            }
        } catch (e: Exception) {
            Log.d("SaveUserManga", "Exception: ${e.message}")
            _uiState.value = _uiState.value.copy(
                state = AddUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
            )
        }
    }



}

data class AddUiState(
    val state: State = State.Idle,
    val userCollectionList: List<Collection> = listOf(),
    val selectedCollectionList: List<Collection> = listOf(),
    val isTitleExpanded: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val readingStatus: List<String> = listOf(),
    val isFavoriteIcon: ImageVector = Icons.Outlined.Star,
    val mangaDetails: MangaDetails = MangaDetails()
) {
    sealed class State {
        data object Loading : State()
        data object Success : State()
        data class Error(val message: String) : State()
        data object Idle : State()
    }
}


data class MangaDetails(
    val userId: Int = 0,
    val mangaId: Int? = null,
    val link: String = "",
    val altLink: String = "",
    val userTitle: String = "",
    val currentChapter: String = "",
    val readingStatus: String = "",
    val dateAdded: LocalDate? = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val isFavorite: Boolean = false,
    val userRating: String = "",
    val notes: String = "",
    val mangadexId: String? = null,
    val coverUrl: String? = null,
    val publicationDate: LocalDate? = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val synopsis: String? = null
)

fun MangaDetails.toUserManga(): UserManga = UserManga(
    userId = userId,
    mangaId = mangaId,
    link = link,
    altLink = altLink,
    userTitle = userTitle,
    currentChapter = currentChapter.toFloat(),
    readingStatus = readingStatus,
    dateAdded = dateAdded,
    isFavorite = isFavorite,
    userRating = userRating.toFloat(),
    notes = notes,
    mangadexId = mangadexId,
    coverUrl = coverUrl,
    publicationDate = publicationDate,
    synopsis = synopsis
)

fun UserManga.toMangaDetails(): MangaDetails = MangaDetails(
    userId = userId,
    mangaId = mangaId,
    link = link,
    altLink = altLink,
    userTitle = userTitle,
    currentChapter = currentChapter.toString(),
    readingStatus = readingStatus,
    dateAdded = dateAdded,
    isFavorite = isFavorite,
    userRating = userRating.toString(),
    notes = notes,
    mangadexId = mangadexId,
    coverUrl = coverUrl,
    publicationDate = publicationDate,
    synopsis = synopsis
)

fun MangaDetails.toMangaBack(): MangaBack = MangaBack(
    mangaId = mangaId ?: 0,
    mangadexId = mangadexId,
    title = userTitle,
    coverUrl = coverUrl ?: "",
    publicationDate = publicationDate,
    synopsis = synopsis ?: ""
)