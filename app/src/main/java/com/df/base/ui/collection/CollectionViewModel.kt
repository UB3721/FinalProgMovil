package com.df.base.ui.collection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.data.MangasRepository
import com.df.base.model.back.Collection
import com.df.base.model.back.User
import com.df.base.ui.add.AddUiState
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
        setUiState()
        if (_collectionUiState.value.newCollection.collectionName == "") {
            _collectionUiState.value = _collectionUiState.value.copy(
                state = CollectionUiState.State.Error(message = "Collection can't be empty")
            )
            return
        }
        viewModelScope.launch {
            _collectionUiState.value = _collectionUiState.value.copy(state = CollectionUiState.State.Loading)

            try {
                val response = mangasRepository.saveCollection(_collectionUiState.value.newCollection)

                if (response.isSuccessful) {
                    val successMessage = response.body()?.message
                    Log.d("saveCollection", "Success: $successMessage")
                    _collectionUiState.value = _collectionUiState.value.copy(
                        state = CollectionUiState.State.Success
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("saveCollection", "Error: $errorBody")
                    _collectionUiState.value = _collectionUiState.value.copy(
                        state = CollectionUiState.State.Error(message = errorBody)
                    )
                }

                _collectionUiState.value = _collectionUiState.value.copy(
                    newCollection = _collectionUiState.value.newCollection.copy(
                        collectionName = ""
                    )
                )
                fetchAllCollections()
            } catch (e: Exception) {
                println("Error saving collection: ${e.message}")
                _collectionUiState.value = _collectionUiState.value.copy(
                    state = CollectionUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

    fun setUiState() {
        _collectionUiState.value = _collectionUiState.value.copy(state = CollectionUiState.State.Loading)
    }

    fun setUser(user: User) {
        _collectionUiState.value = _collectionUiState.value.copy(
            userId = user.userId
        )
    }


    fun fetchAllCollections() {
        viewModelScope.launch {
            _collectionUiState.value = _collectionUiState.value.copy(state = CollectionUiState.State.Loading)

            try {
                val response = mangasRepository.getAllCollection(_collectionUiState.value.userId)

                if (response.isSuccessful) {
                    _collectionUiState.value = _collectionUiState.value.copy(
                        state = CollectionUiState.State.Success,
                        userCollectionList = response.body() ?: listOf()
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _collectionUiState.value = _collectionUiState.value.copy(
                        state = CollectionUiState.State.Error(message = errorBody)
                    )
                }
            } catch (e: Exception) {
                _collectionUiState.value = _collectionUiState.value.copy(
                    state = CollectionUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

}

data class CollectionUiState(
    val state: State = State.Idle,
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
) {
    sealed class State {
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
        object Idle : State()
    }
}
