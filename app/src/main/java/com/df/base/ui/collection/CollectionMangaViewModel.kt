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
            try {
                mangasRepository.deleteMangaCollection(
                    userId = mangaCollection.user.userId,
                    collectionId = mangaCollection.collection.collectionId,
                    mangaId = mangaCollection.manga.mangaId
                )
                _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                    mangaCollectionList = _collectionMangaUiState.value.mangaCollectionList
                )
                fetchAllMangaCollection()
            } catch (e: Exception) {
                println("Error deleting manga: ${e.message}")
            }
        }
    }

    suspend fun fetchUserMangaById(mangaId: Int): UserManga? {
        return try {
            mangasRepository.getUserMangaById(userId = _collectionMangaUiState.value.userId , mangaId = mangaId)
        } catch (e: Exception) {
            println("Error fetching user manga by id: ${e.message}")
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
            try {
                val mangaCollectionList = mangasRepository.getMangaCollection(
                    collectionId = collectionId,
                    userId = _collectionMangaUiState.value.userId
                )
                _collectionMangaUiState.value = _collectionMangaUiState.value.copy(
                    mangaCollectionList = mangaCollectionList
                )
            } catch (e: Exception) {
                println("Error fetching manga collections: ${e.message}")
            }
        }
    }
}

data class CollectionMangaUiState(
    val userId: Int = 0,
    val mangaCollectionList: List<MangaCollection> = listOf()
)