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
            val response = mangasRepository.deleteUserManga(
                userId = userManga.userId,
                mangaId = userManga.mangaId ?: 0
            )
            if (response.isSuccessful) {
                val successMessage = response.body()?.message
                Log.d("DeleteUserManga", "Success: $successMessage")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("DeleteUserManga", "Error: $errorBody")
            }
            fetchUserMangaList()
        }
    }

    fun setUser(user: User) {
        _favoritesViewModel.value = _favoritesViewModel.value.copy(
            userId = user.userId
        )
    }

    fun fetchUserMangaList() {
        viewModelScope.launch {
            try {
                val userMangaList = mangasRepository.getFavoritesUserMangaStream(_favoritesViewModel.value.userId)
                _favoritesViewModel.value = _favoritesViewModel.value.copy(
                    userMangaList = userMangaList
                )
            } catch (e: Exception) {
                _favoritesViewModel.value = FavoritesUiState()
                println("Error fetching favorite user manga list: ${e.message}")
            }
        }
    }
}

data class FavoritesUiState (
    val userId: Int = 0,
    val userMangaList: List<UserManga> = listOf()
)