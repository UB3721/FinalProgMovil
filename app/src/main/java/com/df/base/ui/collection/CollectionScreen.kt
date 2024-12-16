package com.df.base.ui.collection

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.model.back.Collection
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

    LaunchedEffect(Unit) {
        collectionViewModel.fetchAllCollections()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        CollectionBody(
            contentPadding = innerPadding,
            navController = navController,
            collectionList = collectionUiState.userCollectionList,
            showDialog = collectionUiState.showDialog,
            updateShowDialog = { collectionViewModel.updateShowDialog() },
            newCollectionName = collectionUiState.newCollection.collectionName,
            onNameChanged = {collectionViewModel.updateName(it)},
            onCreateCollectionClick = {
                collectionViewModel.addCollection()
                collectionViewModel.updateShowDialog()
            }
        )

    }
}

@Composable
fun CollectionBody(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navController: NavController,
    showDialog: Boolean,
    newCollectionName: String,
    onNameChanged: (String) -> Unit,
    updateShowDialog: () -> Unit,
    collectionList: List<Collection>,
    onCreateCollectionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = updateShowDialog,
            ) {
                Text("Create Collection")
            }
        }

        if (collectionList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                Log.d("tag", collectionList.toString())
                items(collectionList) { collection ->
                    CollectionItem(collection = collection)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = updateShowDialog,
                title = { Text("New collection") },
                text = {
                    OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = onNameChanged,
                        label = { Text("Collection name") }
                    )
                },
                confirmButton = {
                    Button(onClick = { onCreateCollectionClick() }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    Button(onClick = updateShowDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CollectionItem(collection: Collection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = collection.collectionName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}