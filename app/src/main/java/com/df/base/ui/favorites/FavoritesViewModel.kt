package com.df.base.ui.favorites

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

class FavoritesViewModel(private val mangasRepository: MangasRepository): ViewModel() {
    private val _favoritesViewModel = MutableStateFlow(FavoritesUiState())
    val favoritesUiState: StateFlow<FavoritesUiState> = _favoritesViewModel.asStateFlow()


    fun deleteUserManga(userManga: UserManga) {
        viewModelScope.launch {
            _favoritesViewModel.value = _favoritesViewModel.value.copy(state = FavoritesUiState.State.Loading)

            try {
                val response = mangasRepository.deleteUserManga(
                    userId = userManga.userId,
                    mangaId = userManga.mangaId ?: 0
                )

                if (response.isSuccessful) {
                    val successMessage = response.body()?.message
                    Log.d("DeleteUserManga", "Success: $successMessage")
                    _favoritesViewModel.value = _favoritesViewModel.value.copy(state = FavoritesUiState.State.Success)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("DeleteUserManga", "Error: $errorBody")
                    _favoritesViewModel.value = _favoritesViewModel.value.copy(
                        state = FavoritesUiState.State.Error(message = errorBody ?: "Unknown error")
                    )
                }

                fetchUserMangaList()
            } catch (e: Exception) {
                Log.d("DeleteUserManga", "Exception: ${e.message}")
                _favoritesViewModel.value = _favoritesViewModel.value.copy(
                    state = FavoritesUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }


    fun setUser(user: User) {
        _favoritesViewModel.value = _favoritesViewModel.value.copy(
            userId = user.userId
        )
    }

    fun fetchUserMangaList() {
        viewModelScope.launch {
            _favoritesViewModel.value = _favoritesViewModel.value.copy(state = FavoritesUiState.State.Loading)

            try {
                val response = mangasRepository.getFavoritesUserMangaStream(_favoritesViewModel.value.userId)

                if (response.isSuccessful) {
                    _favoritesViewModel.value = _favoritesViewModel.value.copy(
                        state = FavoritesUiState.State.Success,
                        userMangaList = response.body() ?: listOf()
                    )
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _favoritesViewModel.value = _favoritesViewModel.value.copy(
                        state = FavoritesUiState.State.Error(message = errorBody)
                    )
                }
            } catch (e: Exception) {
                Log.d("fetchUserMangaList", "Error fetching favorite user manga list: ${e.message}")
                _favoritesViewModel.value = _favoritesViewModel.value.copy(
                    state = FavoritesUiState.State.Error(message = e.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

}

data class FavoritesUiState(
    val state: State = State.Idle,
    val userId: Int = 0,
    val userMangaList: List<UserManga> = listOf()
) {
    sealed class State {
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
        object Idle : State()
    }
}
