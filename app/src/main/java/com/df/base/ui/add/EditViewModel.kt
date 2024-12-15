package com.df.base.ui.add

import android.util.Log
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.df.base.data.MangasRepository
import com.df.base.model.back.UserManga
import com.df.base.ui.SelectedManga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditViewModel(private val mangasRepository: MangasRepository): ViewModel() {
    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

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
            mangaDetails = uiState.value.mangaDetails
                .copy(isFavorite = !_uiState.value.mangaDetails.isFavorite)
        )
    }

    fun initializeState(
        userManga: UserManga
    ) {
        Log.d("tag", userManga.toString())
        _uiState.value = _uiState.value.copy(mangaDetails = userManga.toMangaDetails())
    }

    suspend fun updateUserManga() {
        try {
            val response = mangasRepository.updateUserManga(uiState.value.mangaDetails.toUserManga())

            if (response.isSuccessful) {
                val successMessage = response.body()?.message
                Log.d("updateUserManga", "Success: $successMessage")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("updateUserManga", "Error: $errorBody")
            }
        } catch (e: Exception) {
            Log.d("updateUserManga", "Exception: ${e.message}")
        }
    }
}

data class EditUiState(
    val isTitleExpanded: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val readingStatus: List<String> = listOf(),
    val mangaDetails: MangaDetails = MangaDetails()
)