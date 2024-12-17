package com.df.base.ui.add

import android.util.Log
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.data.MangasRepository
import com.df.base.model.back.Collection
import com.df.base.model.back.MangaCollection
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
                for (collection in _uiState.value.selectedCollectionList) {
                    Log.d("tag", _uiState.value.mangaDetails.toMangaBack().toString())
                    val response = mangasRepository.saveMangaCollection(
                        MangaCollection(
                            collection = collection,
                            manga = _uiState.value.mangaDetails.toMangaBack(),
                            user = User(userId = 1, userName = "")
                        )
                    )

                    if (response.isSuccessful) {
                        val successMessage = response.body()?.message
                        Log.d("SaveCollection", "Success: $successMessage")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("SaveCollection", "Error saving collection:, Error: $errorBody")
                    }
                }
            } catch (e: Exception) {
                println("Error saving collections: ${e.message}")
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
            try {
                val collectionList = mangasRepository.getAllCollection(_uiState.value.mangaDetails.userId)
                _uiState.value = _uiState.value.copy(
                    userCollectionList = collectionList
                )
            } catch (e: Exception) {
                println("Error fetching collections: ${e.message}")
            }
        }
    }

    fun setUser(user: User) {
        _uiState.value = _uiState.value.copy(
            mangaDetails = _uiState.value.mangaDetails.copy(
                userId = user.userId
            )
        )
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
            saveSelectedCollections()
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