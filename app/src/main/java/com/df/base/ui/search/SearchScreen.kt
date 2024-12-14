package com.df.base.ui.search

import MangaUiState
import SearchViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.calculateEndPadding
//import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.df.base.ui.SelectedManga
import com.df.base.ui.add.AddDestination
import com.df.base.ui.navigation.NavigationDestination
import com.df.base.model.mangadex.Manga

object SearchDestination: NavigationDestination {
    override  val route = "search"
    override  val titleRes = R.string.search_list
    override  val icon = Icons.Default.Search
}

@Composable
fun MangaScreen(
    navController: NavController,
    mangaViewModel: SearchViewModel = viewModel()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController
            )
        }
    ) { innerPadding ->
        SearchBody(
            mangaViewModel.mangaUiState,
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
    mangaViewModel: SearchViewModel,
    navController: NavController,
    modifier: Modifier
) {
    Column(modifier = Modifier) {
        SearchBar(mangaViewModel)
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
                                viewModel = mangaViewModel,
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
    viewModel: SearchViewModel,
    navController: NavController
) {
    val title = manga.attributes.title?.get("en") ?: stringResource(R.string.no_title)
    val description = manga.attributes.description?.get("en") ?: stringResource(R.string.no_description)
    viewModel.fetchImg(manga)
    val coverUrl = viewModel.coverUrl ?: ""

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.dialog_add_manga)) },
            text = { Text(text = title) },
            confirmButton = {
                TextButton(onClick = {
                    val selectedManga = SelectedManga(
                        id = manga.id,
                        title = title,
                        synopsis = description,
                        coverUrl = coverUrl
                    )
                    navController.navigate(AddDestination.routeWithArgs(selectedManga))
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                model = viewModel.coverUrl,
                contentDescription = stringResource(R.string.cover),
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(8.dp)
                //error = painterResource(id = R.drawable.placeholder_image),
                //fallback = painterResource(id = R.drawable.placeholder_image)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                var isExpanded by remember { mutableStateOf(false) }
                Text(
                    text = description,
                    fontSize = 14.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }
        }
    }
}





@Composable
fun SearchBar(
    mangaViewModel: SearchViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = mangaViewModel.query,
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
            onClick = { mangaViewModel.fetchManga(mangaViewModel.query) },
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