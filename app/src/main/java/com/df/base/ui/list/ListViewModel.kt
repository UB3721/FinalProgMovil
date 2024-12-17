package com.df.base.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.data.MangasRepository
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

    fun fetchUserMangaList() {
        viewModelScope.launch {
            try {
                val userMangaList = mangasRepository.getUserMangaStream(1)
                _listUiState.value = ListUiState(userMangaList)
            } catch (e: Exception) {
                _listUiState.value = ListUiState()
                println("Error fetching user manga list: ${e.message}")
            }
        }
    }
}

data class ListUiState(
    val userMangaList: List<UserManga> = listOf()
)