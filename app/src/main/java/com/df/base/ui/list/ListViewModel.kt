package com.df.base.ui.list

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

data class ListUiState(val userMangaList: List<UserManga> = listOf())