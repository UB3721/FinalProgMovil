package com.df.base.ui.list

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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

object ListDestination: NavigationDestination {
    override  val route = "list"
    override  val titleRes = R.string.list
    override  val icon = Icons.AutoMirrored.Filled.List
}

@Composable
fun ListScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    navToDisplay: (UserManga) -> Unit,
    listViewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by listViewModel.listUiState.collectAsState()

    when (val state = listUiState.state) {
        is ListUiState.State.Error -> {
            ShowWarningAlert(state.message)
        }
        else -> {}
    }

    LaunchedEffect(Unit) {
        listViewModel.setUser(
            User(
                userId = loginViewModel.user.value?.userId!!,
                userName = loginViewModel.user.value?.userName!!
            )
        )
        listViewModel.fetchUserMangaList()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        UiListBody(
            title = stringResource(R.string.delete),
            mangaList = listUiState.userMangaList,
            contentPadding = innerPadding,
            navToDisplay = navToDisplay,
            onDialogActionUserManga = {
                listViewModel.setUiState()
                listViewModel.deleteUserManga(it)
                                      },
            collection = null,
        )
    }
}