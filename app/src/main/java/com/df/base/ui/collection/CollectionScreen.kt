package com.df.base.ui.collection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.navigation.NavigationDestination

object CollectionDestination: NavigationDestination {
    override  val route = "collection"
    override  val titleRes = R.string.collection
    override  val icon = Icons.Default.Email
}

@Composable
fun CollectionScreen(
    navController: NavController,
    collectionViewModel: CollectionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val collectionUiState by collectionViewModel.collectionUiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        CollectionBody(
            contentPadding = innerPadding,
            navController = navController
        )

    }
}

@Composable
fun CollectionBody(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navController: NavController,
) {

}