import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.df.base.network.MangasApi
import com.df.base.model.mangadex.Manga
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    var mangaUiState by mutableStateOf<MangaUiState>(MangaUiState.Loading)
        private set

    var isFullText by mutableStateOf(false)
        private set

    var query by mutableStateOf("")
        private set

    var coverUrl by mutableStateOf<String?>(null)
        private set

    fun updateQuery(newQuery: String) {
        query = newQuery
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

    fun displayFullText(fullText: Boolean) {
        isFullText = fullText
    }

    fun fetchImg(manga: Manga) {
        val relationships = manga.relationships
        val coverArtRelationship = relationships.find { it.type == "cover_art" }
        val coverAttributes = coverArtRelationship?.attributes
        coverUrl = coverAttributes?.fileName?.let { filename ->
            "https://uploads.mangadex.org/covers/${manga.id}/$filename"
        }
    }
}

sealed interface MangaUiState {
    object Loading : MangaUiState
    data class Success(val mangas: List<Manga>) : MangaUiState
    data class Error(val message: String) : MangaUiState
}
