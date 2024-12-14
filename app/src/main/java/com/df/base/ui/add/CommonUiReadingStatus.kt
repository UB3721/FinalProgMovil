package com.df.base.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.df.base.R

@Composable
fun UiReadingStatus(
    userManga: MangaDetails,
    onDropdownMenuClick: (Boolean) -> Unit = {},
    onDropDownItemClick: (String) -> Unit = {},
    isDropdownExpanded: Boolean = false,
    readingStatus: List<String> = listOf(),
    isEditable: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.reading_status),
                style = MaterialTheme.typography.titleMedium
            )

            if (isEditable) {
                TextButton(
                    onClick = { onDropdownMenuClick(true) },
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = userManga.readingStatus,
                    )
                }
            } else {
                Text(
                    text = userManga.readingStatus,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp)
                )
            }
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { onDropdownMenuClick(false) }
        ) {
            readingStatus.forEach { status ->
                DropdownMenuItem(
                    onClick = {
                        onDropDownItemClick(status)
                    },
                    text = { Text(status) },
                    modifier = Modifier.padding(0.dp),
                    leadingIcon = null,
                    trailingIcon = null,
                    enabled = true,
                )
            }
        }
    }
}
