package com.df.base.ui.collection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.data.MangasRepository
import com.df.base.model.back.Collection
import com.df.base.model.back.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionViewModel(private val mangasRepository: MangasRepository) : ViewModel() {
    private val _collectionUiState = MutableStateFlow(CollectionUiState())
    val collectionUiState: StateFlow<CollectionUiState> = _collectionUiState.asStateFlow()

    fun updateShowDialog() {
        _collectionUiState.value = _collectionUiState.value.copy(
            showDialog = !_collectionUiState.value.showDialog
        )
    }

    fun updateName(newName: String) {
        _collectionUiState.value = _collectionUiState.value.copy(
            newCollection = _collectionUiState.value.newCollection.copy(
                collectionName = newName
            ),
        )
    }

    fun updateEditName(newEditName: String) {
        _collectionUiState.value = _collectionUiState.value.copy(
            editNewName = newEditName
        )
    }

    fun addCollection() {
        viewModelScope.launch {
            try {
                val response = mangasRepository.saveCollection(_collectionUiState.value.newCollection)
                if (response.isSuccessful) {
                    val successMessage = response.body()?.message
                    Log.d("saveCollection", "Success: $successMessage")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("saveCollection", "Error: $errorBody")
                }
                _collectionUiState.value = _collectionUiState.value.copy(
                    newCollection = _collectionUiState.value.newCollection.copy(
                        collectionName = ""
                    )
                )
                fetchAllCollections()
            } catch (e: Exception) {
                println("Error saving collection: ${e.message}")
            }
        }
    }

    fun setUser(user: User) {
        _collectionUiState.value = _collectionUiState.value.copy(
            userId = user.userId
        )
    }


    fun fetchAllCollections() {
        viewModelScope.launch {
            try {
                val collectionList = mangasRepository.getAllCollection(_collectionUiState.value.userId)
                _collectionUiState.value = _collectionUiState.value.copy(
                    userCollectionList = collectionList
                )
            } catch (e: Exception) {
                println("Error fetching collections: ${e.message}")
            }
        }
    }
}

data class CollectionUiState(
    val userId: Int = 0,
    val editNewName: String = "",
    val newCollection: Collection = Collection(
        collectionId = 0,
        userId = 1,
        collectionName = "",
        dateCreated = null,
        dateLastModified = null
    ),
    val showDialog: Boolean = false,
    val userCollectionList: List<Collection> = listOf()
)
