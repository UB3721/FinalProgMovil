package com.df.base.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope

import com.df.base.data.MangasRepository
import com.df.base.model.back.MangaCollection
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionMangaViewModel(
    savedStateHandle: SavedStateHandle,
    private val mangasRepository: MangasRepository
): ViewModel() {
    private val _collectionMangaUiState = MutableStateFlow(CollectionMangaUiState())
    val collectionMangaUiState: StateFlow<CollectionMangaUiState> = _collectionMangaUiState.asStateFlow()

    private val collectionId: Int = checkNotNull(savedStateHandle[CollectionMangaDestination.collectionIdArg])


    fun deleteMangaFromCollection(mangaCollection: MangaCollection) {
        viewModelScope.launch {
            _collectionMangaUiState.value = _collectionMangaUiState.value.copy(state = CollectionMangaUiState.State.Loading)
            try {
                val response = mangasRepository.deleteMangaCollection(
                    userId = mangaCollection.user.userId,
                    collectionId = mangaCollection.collection.collectionId,
                    mangaId = mangaCollection.manga.mangaId
                )
                if (response.isSuccessful) {
                    _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                        state = CollectionMangaUiState.State.Success
                    )
                    fetchAllMangaCollection()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                        state = CollectionMangaUiState.State.Error(message = errorBody)
                    )
                }
            } catch (e: Exception) {
                _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                    state = CollectionMangaUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }


    suspend fun fetchUserMangaById(mangaId: Int): UserManga? {
        _collectionMangaUiState.value = _collectionMangaUiState.value.copy(state = CollectionMangaUiState.State.Loading)

        return try {
            val response = mangasRepository.getUserMangaById(userId = _collectionMangaUiState.value.userId, mangaId = mangaId)

            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                    state = CollectionMangaUiState.State.Error(message = errorBody)
                )
                null
            }
        } catch (e: Exception) {
            _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                state = CollectionMangaUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
            )
            null
        }
    }


    fun setUser(user: User) {
        _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
            userId = user.userId
        )
    }

    fun fetchAllMangaCollection() {
        viewModelScope.launch {
            _collectionMangaUiState.value = _collectionMangaUiState.value.copy(state = CollectionMangaUiState.State.Loading)

            try {
                val response = mangasRepository.getMangaCollection(
                    collectionId = collectionId,
                    userId = _collectionMangaUiState.value.userId
                )
                if (response.isSuccessful) {
                    _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                        state = CollectionMangaUiState.State.Success,
                        mangaCollectionList = response.body() ?: listOf()
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                        state = CollectionMangaUiState.State.Error(message = errorBody)
                    )
                }
            } catch (e: Exception) {
                _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                    state = CollectionMangaUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

}

data class CollectionMangaUiState(
    val state: State = State.Idle,
    val userId: Int = 0,
    val mangaCollectionList: List<MangaCollection> = listOf()
) {
    sealed class State {
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
        object Idle : State()
    }
}
