package com.df.base.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.df.base.R
import com.df.base.model.back.Collection
import com.df.base.model.back.UserManga


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UiItemList(
    title: String,
    userManga: UserManga,
    collection: Collection?,
    navToDisplay: (UserManga) -> Unit,
    onDialogActionUserManga: (UserManga) -> Unit = {},
    onDialogActionCollection: (Collection) -> Unit = {}
) {
    val showDialog = rememberSaveable() { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = userManga.userTitle)
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (collection!=null) {
                            onDialogActionCollection(collection)
                        } else {
                            onDialogActionUserManga(userManga)
                        }
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text(text =  stringResource(R.string.cancel))
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { navToDisplay(userManga) },
                onLongClick = { showDialog.value = true }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = userManga.coverUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = userManga.userTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = userManga.readingStatus,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "ch ${userManga.currentChapter}",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = userManga.userRating.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}