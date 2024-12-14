package com.df.base.ui.collection

import androidx.lifecycle.ViewModel
import com.df.base.data.MangasRepository
import com.df.base.model.back.MangaCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectionViewModel(private val mangasRepository: MangasRepository): ViewModel() {
    private val _collectionUiState = MutableStateFlow(CollectionUiState())
    val collectionUiState: StateFlow<CollectionUiState> = _collectionUiState.asStateFlow()
}

data class CollectionUiState (val userMangaList: List<MangaCollection> = listOf())