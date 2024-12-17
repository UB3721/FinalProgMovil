package com.df.base.ui.collection

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    navToCollectionManga: (Int) -> Unit,
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
            onNewEditNameChanged = {collectionViewModel.updateEditName(it)},
            showDialog = collectionUiState.showDialog,
            updateShowDialog = { collectionViewModel.updateShowDialog() },
            newCollectionName = collectionUiState.newCollection.collectionName,
            onNameChanged = {collectionViewModel.updateName(it)},
            onCreateCollectionClick = {
                collectionViewModel.addCollection()
                collectionViewModel.updateShowDialog()
            },
            navToCollectionManga = navToCollectionManga
        )

    }
}

@Composable
fun CollectionBody(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navController: NavController,
    showDialog: Boolean,
    onNewEditNameChanged: (String) -> Unit,
    newCollectionName: String,
    onNameChanged: (String) -> Unit,
    updateShowDialog: () -> Unit,
    collectionList: List<Collection>,
    navToCollectionManga: (Int) -> Unit,
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
                items(collectionList) { collection ->
                    CollectionItem(
                        collection = collection,
                        onNewEditNameChanged = onNewEditNameChanged,
                        onCollectionClick = navToCollectionManga,
                        onEditCollection = {},
                        onDeleteCollection = {}
                    )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionItem(
    collection: Collection,
    onNewEditNameChanged: (String) -> Unit,
    onCollectionClick: (Int) -> Unit,
    onEditCollection: () -> Unit,
    onDeleteCollection: () -> Unit
) {
    var showEditDeleteDialog by rememberSaveable() { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { onCollectionClick(collection.collectionId) },
                onLongClick = { showEditDeleteDialog = true }
            )
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

    if (showEditDeleteDialog) {
        onNewEditNameChanged(collection.collectionName)
        EditDeleteDialog(
            collectionName = collection.collectionName,
            onNewNameChanged = { onNewEditNameChanged(it) },
            onEditClick = {
                onEditCollection()
                showEditDeleteDialog = false
            },
            onDeleteClick = {
                onDeleteCollection()
                showEditDeleteDialog = false
            },
            onDismiss = { showEditDeleteDialog = false }
        )
    }
}

@Composable
fun EditDeleteDialog(
    collectionName: String,
    onNewNameChanged: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    var newEditName by rememberSaveable() { mutableStateOf(collectionName) }

    onNewNameChanged(newEditName)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit or Delete Collection") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = newEditName,
                        onValueChange = { newName -> newEditName = newName },
                        label = { Text("Edit Collection Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            },
            modifier = Modifier
                .width(350.dp)
                .wrapContentHeight()
        )
    }
}




