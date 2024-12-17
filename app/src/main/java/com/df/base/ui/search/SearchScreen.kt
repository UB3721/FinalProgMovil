package com.df.base.ui.search

import MangaUiState
import SearchUiState
import SearchViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.calculateEndPadding
//import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.ui.add.AddDestination
import com.df.base.ui.navigation.NavigationDestination
import com.df.base.model.mangadex.Manga
import com.df.base.ui.CommonDialog
import com.df.base.ui.MangaAction
import com.df.base.ui.login.LoginViewModel

object SearchDestination: NavigationDestination {
    override  val route = "search"
    override  val titleRes = R.string.search_list
    override  val icon = Icons.Default.Search
}

@Composable
fun MangaScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    mangaViewModel: SearchViewModel = viewModel()
) {
    val searchUiState by mangaViewModel.searchUiState.collectAsState()


    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController
            )
        }
    ) { innerPadding ->
        SearchBody(
            mangaViewModel.mangaUiState,
            searchUiState,
            mangaViewModel,
            navController,
            modifier = Modifier
                .padding( innerPadding
//                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
//                    top = innerPadding.calculateTopPadding(),
//                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
        )
    }

}

@Composable
fun SearchBody(
    uiState: MangaUiState,
    searchUiState: SearchUiState,
    mangaViewModel: SearchViewModel,
    navController: NavController,
    modifier: Modifier
) {
    Column(modifier = Modifier) {
        SearchBar(
            mangaViewModel = mangaViewModel,
            searchUiState = searchUiState
        )
        when (uiState) {
            is MangaUiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.waiting_for_search),
                        modifier = modifier
                            .align(Alignment.Center),
                        fontSize = 18.sp
                    )
                }
            }
            is MangaUiState.Success -> {
                val mangas = uiState.mangas
                if (mangas.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_results_found),
                        modifier = modifier
                            .fillMaxWidth(),
                        fontSize = 18.sp
                    )
                } else {
                    LazyColumn(modifier = modifier) {
                        items(items = mangas, key = {it.id}) { manga ->
                            MangaItem(
                                manga = manga,
                                searchUiState = searchUiState,
                                searchViewModel = mangaViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
            is MangaUiState.Error -> {
                val errorMessage = uiState.message
                Text(
                    text = errorMessage,
                    modifier = modifier
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp
                )
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaItem(
    manga: Manga,
    searchUiState: SearchUiState,
    searchViewModel: SearchViewModel,
    navController: NavController
) {
    var showDialog by rememberSaveable() { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { showDialog = true }
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = manga.relationships.find { it.type == "cover_art" }?.attributes?.fileName?.let {
                    "https://uploads.mangadex.org/covers/${manga.id}/$it"
                },
                contentDescription = stringResource(R.string.cover),
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
            ) {
                Text(
                    text = manga.attributes.title?.get("en") ?: stringResource(R.string.no_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = manga.attributes.description?.get("en") ?: stringResource(R.string.no_description),
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (showDialog) {
        searchUiState.selectedManga
        searchViewModel.updateId(manga.id)
        searchViewModel.updateTitle(manga.attributes.title?.get("en") ?: stringResource(R.string.no_title))
        searchViewModel.updateDesc(manga.attributes.description?.get("en") ?: stringResource(R.string.no_description))
        searchViewModel.fetchImg(manga)

        CommonDialog(
            title = stringResource(R.string.dialog_add_manga),
            mangaAction = MangaAction.Add(
                searchUiState.selectedManga
            ),
            showDialog = showDialog,
            onShowDialogChanged = { showDialog = false },
            navigateToAdd = { navController.navigate(AddDestination.routeWithArgs(searchUiState.selectedManga)) }
        )
    }
}






@Composable
fun SearchBar(
    mangaViewModel: SearchViewModel,
    searchUiState: SearchUiState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchUiState.query,
            onValueChange = { mangaViewModel.updateQuery(it) },
            label = { Text( stringResource(R.string.description_search)) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.Blue
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
        )
        Button(
            onClick = { mangaViewModel.fetchManga(searchUiState.query) },
            modifier = Modifier
                .padding(2.dp)
                .height(IntrinsicSize.Min)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.description_search)
            )
        }
    }
}