package com.df.base.ui.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.model.back.MangaCollection
import com.df.base.model.back.Collection
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.ShowWarningAlert
import com.df.base.ui.UiItemList
import com.df.base.ui.login.LoginViewModel
import com.df.base.ui.navigation.NavigationDestination

object CollectionMangaDestination: NavigationDestination {
    override val route = "collection_manga"
    override val titleRes = R.string.collection_manga
    override val icon: ImageVector? = null
    const val collectionIdArg ="collectionId"
    val routeWithArgs = "$route/{$collectionIdArg}"
}

@Composable
fun CollectionMangaScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    navToDisplay: (UserManga) -> Unit,
    collectionMangaViewModel: CollectionMangaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val collectionMangaUiState by collectionMangaViewModel.collectionMangaUiState.collectAsState()

    LaunchedEffect(Unit) {
        collectionMangaViewModel.setUser(
            User(
                userId = loginViewModel.user.value?.userId!!,
                userName = loginViewModel.user.value?.userName!!
            )
        )
        collectionMangaViewModel.fetchAllMangaCollection()
    }

    when (val state = collectionMangaUiState.state) {
        is CollectionMangaUiState.State.Error -> {
            ShowWarningAlert(state.message)
        }
        else -> {}
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        CollectionMangaBody(
            contentPadding = innerPadding,
            navToDisplay = navToDisplay,
            mangaCollectionList = collectionMangaUiState.mangaCollectionList,
            getUserManga = { mangaId -> collectionMangaViewModel.fetchUserMangaById(mangaId) },
            onMangaCollectionDelete = { collectionMangaViewModel.deleteMangaFromCollection(it) }
        )
    }
}

@Composable
fun CollectionMangaBody(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mangaCollectionList: List<MangaCollection>,
    navToDisplay: (UserManga) -> Unit,
    getUserManga: suspend (Int) -> UserManga?,
    onMangaCollectionDelete: (MangaCollection) -> Unit
) {
    Column(modifier = Modifier.padding(contentPadding)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (mangaCollectionList.isEmpty()) {
                Text(stringResource(R.string.empty_user_manga_list))
            } else {
                Text(text = mangaCollectionList[0].collection.collectionName)
            }
        }
        LazyColumn {
            items(mangaCollectionList) { mangaCollection ->
                MangaItem(
                    mangaCollection = mangaCollection,
                    onLongClick = { onMangaCollectionDelete(mangaCollection) },
                    getUserManga = getUserManga,
                    onClick = navToDisplay
                )
            }
        }

    }
}

@Composable
fun MangaItem(
    mangaCollection: MangaCollection,
    getUserManga: suspend (Int) -> UserManga?,
    onClick: (UserManga) -> Unit,
    onLongClick: (Collection) -> Unit
) {
    val userMangaState = rememberSaveable() { mutableStateOf<UserManga?>(null) }

    LaunchedEffect(mangaCollection.manga.mangaId) {
        val userManga = getUserManga(mangaCollection.manga.mangaId)
        if (userManga != null) {
            userMangaState.value = userManga
        }
    }

    userMangaState.value?.let { userManga ->
        UiItemList(
            title = stringResource(R.string.collection_delete),
            userManga = userManga,
            navToDisplay = { onClick(userManga) },
            onDialogActionCollection = onLongClick,
            collection = mangaCollection.collection
        )
    }
}
