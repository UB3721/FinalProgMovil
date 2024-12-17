package com.df.base.ui.add

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.df.base.model.back.Collection
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
import com.df.base.model.back.User
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.SelectedManga
import com.df.base.ui.login.LoginViewModel
import com.df.base.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

object AddDestination : NavigationDestination {
    override val route = "add_screen"
    const val selectedMangaArg = "selectedMangaArg"
    override val titleRes = R.string.add_element
    override val icon = null
    fun routeWithArgs(selectedManga: SelectedManga): String {
        val json = Uri.encode(Json.encodeToString(SelectedManga.serializer(), selectedManga))
        return "$route/$json"
    }
}

@Composable
fun AddScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    navigateBack: () -> Unit,
    selectedManga: SelectedManga,
    addViewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val coroutineScope = rememberCoroutineScope()
    val uiState by addViewModel.uiState.collectAsState()

    val readingStatus = listOf(
        stringResource(R.string.reading),
        stringResource(R.string.completed),
        stringResource(R.string.dropped),
        stringResource(R.string.on_hold)
    )

    LaunchedEffect(Unit) {
        addViewModel.setUser(
            User(
                userId = loginViewModel.user.value?.userId!!,
                userName = loginViewModel.user.value?.userName!!
            )
        )
        addViewModel.initializeState(
            readingStatus = readingStatus,
            selectedStatus = readingStatus[0],
            selectedManga = selectedManga
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        AddScreenContent(
            userManga = uiState.mangaDetails,
            isTitleExpanded = uiState.isTitleExpanded,
            isDropdownExpanded = uiState.isDropdownExpanded,
            readingStatus = uiState.readingStatus,
            isFavorite = uiState.mangaDetails.isFavorite,
            onTitleClick = { addViewModel.updateIsTitleExpanded() },
            onDropdownMenuClick = { addViewModel.updateIsDropdownExpanded(it) },
            onDropDownItemClick = { selectedStatus ->
                addViewModel.updateSelectedStatus(selectedStatus)
                addViewModel.updateIsDropdownExpanded(false)
            },
            onSiteChanged = {addViewModel.updateSiteLink(it)},
            onAltSiteChanged = {addViewModel.updateAltSiteLink(it)},
            onCurrentChapterChanged = {addViewModel.updateCurrentChapter(it)},
            onNotesChanged = {addViewModel.updateNotes(it) },
            onRatingChanged = {addViewModel.updateRating(it)},
            onSaveClick = {
                coroutineScope.launch {
                    addViewModel.saveUserManga()
                    navigateBack()
                }
            },
            onFavoriteChanged = {addViewModel.updateIsFavoriteChanged()},
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
fun AddScreenContent(
    modifier: Modifier = Modifier,
    userManga: MangaDetails,
    isTitleExpanded: Boolean,
    isDropdownExpanded: Boolean,
    readingStatus: List<String>,
    isFavorite: Boolean,
    onTitleClick: () -> Unit,
    onDropdownMenuClick: (Boolean) -> Unit,
    onDropDownItemClick: (String) -> Unit,
    onSiteChanged: (String) -> Unit,
    onAltSiteChanged: (String) -> Unit,
    onCurrentChapterChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onRatingChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onFavoriteChanged: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        UiFavorite(
            isFavorite = isFavorite,
            onFavoriteChanged = onFavoriteChanged,
            isEditable = true
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
