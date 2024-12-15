import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.network.MangasApi
import com.df.base.model.mangadex.Manga
import com.df.base.ui.SelectedManga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    var mangaUiState by mutableStateOf<MangaUiState>(MangaUiState.Loading)
        private set

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState

    fun updateQuery(newQuery: String) {
        _searchUiState.value = _searchUiState.value.copy(query = newQuery)
    }

    fun updateTitle(newTitle: String) {
        _searchUiState.value = _searchUiState.value.copy(
            selectedManga = _searchUiState.value.selectedManga.copy(
                title = newTitle
            )
        )
    }

    fun updateId(newId: String) {
        _searchUiState.value = _searchUiState.value.copy(
            selectedManga = _searchUiState.value.selectedManga.copy(
                id = newId
            )
        )
    }

    fun updateDesc(newDesc: String) {
        _searchUiState.value = _searchUiState.value.copy(
            selectedManga = _searchUiState.value.selectedManga.copy(
                synopsis = newDesc
            )
        )
    }

    fun fetchManga(query: String) {
        mangaUiState = MangaUiState.Loading
        viewModelScope.launch {
            try {
                val mangaList = MangasApi.retrofitService.getMangas(query)
                val mangas = mangaList.data
                mangaUiState = MangaUiState.Success(mangas)
            } catch (e: Exception) {
                mangaUiState = MangaUiState.Error("Error al buscar mangas: ${e.message}")
                Log.e("MangaViewModel", "Error fetching mangas", e)
            }
        }
    }

    fun fetchImg(manga: Manga) {
        val relationships = manga.relationships
        val coverArtRelationship = relationships.find { it.type == "cover_art" }
        val coverAttributes = coverArtRelationship?.attributes
        _searchUiState.value = _searchUiState.value.copy(
            selectedManga = _searchUiState.value.selectedManga.copy(
                coverUrl = coverAttributes?.fileName?.let {
                        filename -> "https://uploads.mangadex.org/covers/${manga.id}/$filename"
                }?: ""
            )
        )
    }
}

data class SearchUiState(
    val selectedManga: SelectedManga = SelectedManga(
        id = "",
        title = "",
        synopsis = "",
        coverUrl = ""
    ),
    val query: String = ""
)

sealed interface MangaUiState {
    object Loading : MangaUiState
    data class Success(val mangas: List<Manga>) : MangaUiState
    data class Error(val message: String) : MangaUiState
}
