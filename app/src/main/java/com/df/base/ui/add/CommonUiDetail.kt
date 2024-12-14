package com.df.base.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.df.base.R
import com.df.base.model.back.User

@Composable
fun UiDetail(
    userManga: MangaDetails,
    selectedUser: DisplayUser = DisplayUser(),
    userList: List<User> = listOf(),
    onSiteChanged: (String) -> Unit = {},
    onAltSiteChanged: (String) -> Unit = {},
    onCurrentChapterChanged: (String) -> Unit = {},
    onRatingChanged: (String) -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onSharedClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
    onSelectedChanged: (User, MangaDetails) -> Unit = { _, _ -> },
    isEditable: Boolean,
    modifier: Modifier = Modifier
) {
    var showShareDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.label_links),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )
        if (isEditable) {
            TextField(
                value = userManga.link,
                onValueChange = onSiteChanged,
                label = { Text(stringResource(R.string.site_link)) },
                modifier = modifier.fillMaxWidth()
            )
            TextField(
                value = userManga.altLink,
                onValueChange = onAltSiteChanged,
                label = { Text(stringResource(R.string.alt_site_link)) },
                modifier = modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.main_link_display, userManga.link))
                    Text(text = stringResource(R.string.alt_link_display, userManga.altLink))
                }
                IconButton(
                    onClick = {
                        onSharedClicked()
                        showShareDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.status_info),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )

        if (isEditable) {
            TextField(
                value = userManga.currentChapter,
                onValueChange = onCurrentChapterChanged,
                label = { Text(stringResource(R.string.current_chapter)) },
                modifier = modifier.fillMaxWidth()
            )
            TextField(
                value = userManga.userRating,
                onValueChange = onRatingChanged,
                label = { Text(stringResource(R.string.rating_label)) },
                modifier = modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = stringResource(R.string.current_chapter_display, userManga.currentChapter),
                modifier = modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(8.dp),
            )
            Text(
                text = stringResource(R.string.rating_display, userManga.userRating),
                modifier = modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(8.dp),
            )
        }

        Text(
            text = stringResource(R.string.notes),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )
        if (isEditable) {
            TextField(
                value = userManga.notes,
                onValueChange = onNotesChanged,
                placeholder = { Text(stringResource(R.string.add_notes_here)) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )
        } else {
            Text(
                text = userManga.notes,
                modifier = modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(8.dp),
            )
        }
    }

    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = {
                Text(text = stringResource(R.string.select_user_to_share))
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(userList) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectedChanged(user, userManga)
                                }
                                .background(
                                    if (selectedUser.userId == user.userId) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.selected_user, user.userName, user.userId),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (selectedUser.userId == user.userId) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSaveClicked()
                        showShareDialog = false
                    },
                    enabled = selectedUser.userId != 0
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

}

