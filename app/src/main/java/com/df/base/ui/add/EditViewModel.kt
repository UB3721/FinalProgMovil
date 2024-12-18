package com.df.base.ui.add

import android.util.Log
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.data.MangasRepository
import com.df.base.model.back.Collection
import com.df.base.model.back.MangaCollection
import com.df.base.model.back.MangaCollectionRequest
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.ui.SelectedManga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        _uiState.value = _uiState.value.copy(mangaDetails = userManga.toMangaDetails())
        fetchAllCollections()
    }

    private fun saveSelectedCollections() {
        viewModelScope.launch {
            try {
                val collectionIdList = _uiState.value.selectedCollectionList.map { it.collectionId }

                val mangaCollectionRequest = MangaCollectionRequest(
                    mangaId = _uiState.value.mangaDetails.mangaId ?: 0,
                    userId = _uiState.value.mangaDetails.userId,
                    collectionIdList = collectionIdList
                )

                val response = mangasRepository.saveMangaCollection(mangaCollectionRequest)

                if (response.isSuccessful) {
                    val successMessage = response.body()?.message
                    Log.d("SaveCollection", "Success: $successMessage")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("SaveCollection", "Error saving collections, Error: $errorBody")
                }
            } catch (e: Exception) {
                println("Error saving collections: ${e.message}")
            }
        }
    }

    private fun fetchSelectedMangaCollections() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(state = AddUiState.State.Loading)

            try {
                val response = mangasRepository.getCollectionByMangaId(
                    userId = _uiState.value.mangaDetails.userId,
                    mangaId = _uiState.value.mangaDetails.mangaId ?: 0
                )

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        selectedCollectionList = response.body() ?: listOf()
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("tag", errorBody)
                }
            } catch (e: Exception) {
                Log.d("tag", e.localizedMessage ?: "Unknown error")
            }
        }
    }


    fun toggleCollectionSelection(collection: Collection) {
        if (_uiState.value.selectedCollectionList.contains(collection)) {
            _uiState.value = _uiState.value.copy(
                selectedCollectionList = _uiState.value.selectedCollectionList.filterNot { it == collection }
            )
        } else {
            _uiState.value = _uiState.value.copy(
                selectedCollectionList = _uiState.value.selectedCollectionList + collection
            )
        }
    }

    private fun fetchAllCollections() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(state = AddUiState.State.Loading)

            try {
                val response = mangasRepository.getAllCollection(_uiState.value.mangaDetails.userId)

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        state = AddUiState.State.Success,
                        userCollectionList = response.body() ?: listOf()
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        state = AddUiState.State.Error(message = errorBody)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    state = AddUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
            fetchSelectedMangaCollections()
        }
    }

    fun setUiState() {
        _uiState.value = _uiState.value.copy(state = AddUiState.State.Loading)
    }

    fun setUser(user: User) {
        _uiState.value = _uiState.value.copy(
            mangaDetails = _uiState.value.mangaDetails.copy(
                userId = user.userId
            )
        )
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

    suspend fun updateUserManga() {
        _uiState.value = _uiState.value.copy(state = AddUiState.State.Loading)

        val validationResult = validateUserManga(uiState.value.mangaDetails)

        if (validationResult.isFailure) {
            _uiState.value = _uiState.value.copy(
                state = AddUiState.State.Error(message = validationResult.exceptionOrNull()?.message ?: "Unknown error")
            )
            return
        }

        try {
            val response = mangasRepository.updateUserManga(uiState.value.mangaDetails.toUserManga())

            if (response.isSuccessful) {
                val successMessage = response.body()?.message
                Log.d("updateUserManga", "Success: $successMessage")
                _uiState.value = _uiState.value.copy(state = AddUiState.State.Success)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("updateUserManga", "Error: $errorBody")
                _uiState.value = _uiState.value.copy(
                    state = AddUiState.State.Error(message = errorBody)
                )
            }
            saveSelectedCollections()
        } catch (e: Exception) {
            Log.d("updateUserManga", "Exception: ${e.message}")
            _uiState.value = _uiState.value.copy(
                state = AddUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
            )
        }
    }

}