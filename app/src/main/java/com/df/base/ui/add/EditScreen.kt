package com.df.base.ui.add

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.model.back.Collection
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.ShowWarningAlert
import com.df.base.ui.login.LoginViewModel
import com.df.base.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

object EditDestination : NavigationDestination {
    override val route = "edit_screen"
    const val USERMANGA = "userMangaArg"
    override val titleRes = R.string.add_element
    override val icon = null
    fun routeWithArgs(userManga: UserManga): String {
        val json = Uri.encode(Json.encodeToString(UserManga.serializer(), userManga))
        return "${route}/$json"
    }
}

@Composable
fun EditScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    navigateBack: () -> Unit,
    userManga: UserManga,
    viewModel: EditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.state is AddUiState.State.Error) {
        ShowWarningAlert(
            (uiState.state as AddUiState.State.Error).message,
            onConfirmation = { viewModel.setUiState() }
        )
    } else if (uiState.state is AddUiState.State.Success) {
        navigateBack()
    }


    val readingStatus = listOf(
        stringResource(R.string.reading),
        stringResource(R.string.completed),
        stringResource(R.string.dropped),
        stringResource(R.string.on_hold)
    )

    LaunchedEffect(Unit) {
        viewModel.setUiState()
        viewModel.setUser(
            User(
                userId = loginViewModel.user.value?.userId!!,
                userName = loginViewModel.user.value?.userName!!
            )
        )
        viewModel.initializeState(
            userManga = userManga
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        EditBody(
            userManga = uiState.mangaDetails,
            isTitleExpanded = uiState.isTitleExpanded,
            isDropdownExpanded = uiState.isDropdownExpanded,
            readingStatus = readingStatus,
            onTitleClick = { viewModel.updateIsTitleExpanded() },
            onDropdownMenuClick = { viewModel.updateIsDropdownExpanded(it) },
            onDropDownItemClick = { selectedStatus ->
                viewModel.updateSelectedStatus(selectedStatus)
                viewModel.updateIsDropdownExpanded(false)
            },
            onSiteChanged = {viewModel.updateSiteLink(it)},
            onAltSiteChanged = {viewModel.updateAltSiteLink(it)},
            onCurrentChapterChanged = {viewModel.updateCurrentChapter(it)},
            onNotesChanged = {viewModel.updateNotes(it) },
            onRatingChanged = {viewModel.updateRating(it)},
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateUserManga()
                }
            },
            onFavoriteChanged = {viewModel.updateIsFavoriteChanged()},
            selectedCollection = uiState.selectedCollectionList,
            collectionList = uiState.userCollectionList,
            toggleCollectionSelection = {viewModel.toggleCollectionSelection(it)},
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = innerPadding.calculateBottomPadding()
                )
        )

    }
}

@Composable
fun EditBody(
    modifier: Modifier = Modifier,
    userManga: MangaDetails,
    isTitleExpanded: Boolean,
    isDropdownExpanded: Boolean,
    readingStatus: List<String>,
    onTitleClick: () -> Unit,
    onDropdownMenuClick: (Boolean) -> Unit,
    onDropDownItemClick: (String) -> Unit,
    onSiteChanged: (String) -> Unit,
    onAltSiteChanged: (String) -> Unit,
    onCurrentChapterChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onRatingChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onFavoriteChanged: () -> Unit,
    selectedCollection: List<Collection>,
    collectionList: List<Collection>,
    toggleCollectionSelection: (Collection) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UiFavorite(
            isEditable = true,
            isFavorite = userManga.isFavorite,
            onFavoriteChanged = onFavoriteChanged
        )

        UiHeader(
            coverUrl = userManga.coverUrl,
            title = userManga.userTitle,
            synopsis = userManga.synopsis ?: stringResource(R.string.no_description),
            isTitleExpanded = isTitleExpanded,
            onTitleClick = onTitleClick,
            modifier = modifier
        )

        UiDetail(
            userManga = userManga,
            onSiteChanged = onSiteChanged,
            onAltSiteChanged = onAltSiteChanged,
            onCurrentChapterChanged = onCurrentChapterChanged,
            onRatingChanged = onRatingChanged,
            onNotesChanged = onNotesChanged,
            isEditable = true,
            modifier = modifier
        )

        UiCollection(
            collectionList = collectionList,
            selectedList = selectedCollection,
            toggleCollectionSelection = toggleCollectionSelection
        )

        UiReadingStatus(
            userManga = userManga,
            onDropdownMenuClick = onDropdownMenuClick,
            onDropDownItemClick = onDropDownItemClick,
            isDropdownExpanded = isDropdownExpanded,
            readingStatus = readingStatus,
            isEditable = true,
            modifier = modifier
        )

        Button(
            onClick = onSaveClick,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.submit))
        }
    }
}