package com.df.base.ui.list

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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

object ListDestination: NavigationDestination {
    override  val route = "list"
    override  val titleRes = R.string.list
    override  val icon = Icons.AutoMirrored.Filled.List
}

@Composable
fun ListScreen(
    navController: NavController,
    listViewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by listViewModel.listUiState.collectAsState()

    LaunchedEffect(Unit) {
        listViewModel.fetchUserMangaList()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        UiListBody(
            mangaList = listUiState.userMangaList,
            contentPadding = innerPadding,
            navController = navController
        )
    }
}