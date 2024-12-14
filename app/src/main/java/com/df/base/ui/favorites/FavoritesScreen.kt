package com.df.base.ui.favorites

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.UiListBody
import com.df.base.ui.navigation.NavigationDestination

object FavoritesDestination: NavigationDestination {
    override  val route = "favorites"
    override  val titleRes = R.string.favorites
    override  val icon = Icons.Default.Star
}

@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val favoritesUiState by favoritesViewModel.favoritesUiState.collectAsState()

    LaunchedEffect(Unit) {
        favoritesViewModel.fetchUserMangaList()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        UiListBody(
            mangaList = favoritesUiState.userMangaList,
            contentPadding = innerPadding,
            navController = navController
        )

    }
}


