package com.df.base.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.df.base.ui.add.MangaDetails

sealed class MangaAction {
    data class Add(val selectedManga: SelectedManga) : MangaAction()
    data class Edit(val mangaDetails: MangaDetails) : MangaAction()
}

@Composable
fun CommonDialog(
    title: String,
    mangaAction: MangaAction,
    showDialog: Boolean,
    onShowDialogChanged: () -> Unit,
    navigateToAdd: (SelectedManga) -> Unit = {},
    navigateToEdit: (MangaDetails) -> Unit = {}
) {
    if (showDialog) {

        AlertDialog(
            onDismissRequest = { onShowDialogChanged() },
            title = { Text(text = title) },
            text = {
                Text(text = when (mangaAction) {
                    is MangaAction.Add -> mangaAction.selectedManga.title
                    is MangaAction.Edit -> mangaAction.mangaDetails.userTitle
                })
            },
            confirmButton = {
                TextButton(onClick = {
                    when (mangaAction) {
                        is MangaAction.Add -> navigateToAdd(mangaAction.selectedManga)
                        is MangaAction.Edit -> navigateToEdit(mangaAction.mangaDetails)
                    }
                    onShowDialogChanged()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDialogChanged() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
