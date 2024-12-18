package com.df.base.ui.favorites

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.ShowWarningAlert
import com.df.base.ui.UiListBody
import com.df.base.ui.login.LoginViewModel
import com.df.base.ui.navigation.NavigationDestination

object FavoritesDestination: NavigationDestination {
    override  val route = "favorites"
    override  val titleRes = R.string.favorites
    override  val icon = Icons.Default.Star
}

@Composable
fun FavoritesScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    navToDisplay: (UserManga) -> Unit,
    favoritesViewModel: FavoritesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val favoritesUiState by favoritesViewModel.favoritesUiState.collectAsState()

    when (val state = favoritesUiState.state) {
        is FavoritesUiState.State.Error -> {
            ShowWarningAlert(state.message)
        }
        else -> {}
    }

    LaunchedEffect(Unit) {
        favoritesViewModel.setUser(
            User(
                userId = loginViewModel.user.value?.userId!!,
                userName = loginViewModel.user.value?.userName!!
            )
        )
        favoritesViewModel.fetchUserMangaList()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        UiListBody(
            title = stringResource(R.string.delete),
            mangaList = favoritesUiState.userMangaList,
            contentPadding = innerPadding,
            navToDisplay = navToDisplay,
            onDialogActionUserManga = {favoritesViewModel.deleteUserManga(it)},
            collection = null,
        )

    }
}


