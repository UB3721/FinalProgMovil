package com.df.base.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.data.MangasRepository
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel(private val mangasRepository: MangasRepository): ViewModel() {
    private val _listUiState = MutableStateFlow(ListUiState())
    val listUiState: StateFlow<ListUiState> = _listUiState.asStateFlow()


    fun deleteUserManga(userManga: UserManga) {
        viewModelScope.launch {
            _listUiState.value = _listUiState.value.copy(state = ListUiState.State.Loading)

            try {
                val response = mangasRepository.deleteUserManga(
                    userId = userManga.userId,
                    mangaId = userManga.mangaId ?: 0
                )

                if (response.isSuccessful) {
                    val successMessage = response.body()?.message
                    Log.d("DeleteUserManga", "Success: $successMessage")
                    _listUiState.value = _listUiState.value.copy(state = ListUiState.State.Success)
                    fetchUserMangaList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("DeleteUserManga", "Error: $errorBody")
                    _listUiState.value = _listUiState.value.copy(
                        state = ListUiState.State.Error(message = errorBody ?: "Unknown error")
                    )
                }

            } catch (e: Exception) {
                Log.d("DeleteUserManga", "Exception: ${e.message}")
                _listUiState.value = _listUiState.value.copy(
                    state = ListUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }


    fun setUser(user: User) {
        _listUiState.value = _listUiState.value.copy(
            userId = user.userId
        )
    }

    fun fetchUserMangaList() {
        viewModelScope.launch {
            _listUiState.value = _listUiState.value.copy(state = ListUiState.State.Loading)

            try {
                val response = mangasRepository.getUserMangaStream(_listUiState.value.userId)

                if (response.isSuccessful) {
                    _listUiState.value = _listUiState.value.copy(
                        state = ListUiState.State.Success,
                        userMangaList = response.body() ?: listOf()
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _listUiState.value = _listUiState.value.copy(
                        state = ListUiState.State.Error(message = errorBody)
                    )
                }
            } catch (e: Exception) {
                Log.d("fetchUserMangaList", "Error fetching user manga list: ${e.message}")
                _listUiState.value = _listUiState.value.copy(
                    state = ListUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

}

data class ListUiState(
    val state: State = State.Idle,
    val userId: Int = 0,
    val userMangaList: List<UserManga> = listOf()
) {
    sealed class State {
        data object Loading : State()
        data object Success : State()
        data class Error(val message: String) : State()
        data object Idle : State()
    }
}
