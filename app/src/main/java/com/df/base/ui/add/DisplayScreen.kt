package com.df.base.ui.add

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.df.base.model.back.UserManga
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

object DisplayDestination : NavigationDestination {
    override val route = "display_screen"
    const val USERMANGA = "userMangaArg"
    override val titleRes = R.string.add_element
    override val icon = null
    fun routeWithArgs(userManga: UserManga): String {
        val json = Uri.encode(Json.encodeToString(UserManga.serializer(), userManga))
        return "$route/$json"
    }
}

@Composable
fun DisplayScreen(
    navController: NavController,
    userManga: UserManga,
    navigateToEdit: (MangaDetails) -> Unit,
    displayViewModel: DisplayViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by displayViewModel.displayUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEdit(userManga.toMangaDetails()) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null //stringResource(R.string.edit_item_title),
                )
            }
        },

    ) { innerPadding ->
        DisplayBody(
            userManga = userManga,
            userList = uiState.userList,
            selectedUser = uiState.selectedUser,
            isTitleExpanded = uiState.isDescExpanded,
            onTitleExpanded = { displayViewModel.updateIsDescExpanded() },
            onSharedClicked = {displayViewModel.fetchAllUsers()},
            onSaveClicked = {
                coroutineScope.launch {
                    displayViewModel.saveSharedLink()
                }
            },
            onSelectedChanged = { user, userManga -> displayViewModel.updateSelectedUser(user, userManga) },
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
fun DisplayBody(
    userManga: UserManga,
    userList: List<User>,
    selectedUser: DisplayUser,
    isTitleExpanded: Boolean,
    onTitleExpanded: () -> Unit,
    onSharedClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onSelectedChanged: (User, MangaDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UiFavorite(
            isEditable = false,
            isFavorite = userManga.isFavorite
        )

        UiHeader(
            coverUrl = userManga.coverUrl,
            title = userManga.userTitle,
            synopsis = userManga.synopsis ?: stringResource(R.string.no_description),
            isTitleExpanded =isTitleExpanded,
            onTitleClick = onTitleExpanded,
            modifier = modifier
        )
        UiDetail(
            userManga = userManga.toMangaDetails(),
            isEditable = false,
            modifier = modifier,
            onSharedClicked = onSharedClicked,
            onSaveClicked = onSaveClicked,
            selectedUser = selectedUser,
            onSelectedChanged = onSelectedChanged,
            userList = userList

        )
        UiReadingStatus(
            userManga = userManga.toMangaDetails(),
            isEditable = false,
            modifier = modifier
        )
    }
}