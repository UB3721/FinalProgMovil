package com.df.base.ui.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun UiHeader(
    coverUrl: String?,
    title: String,
    synopsis: String,
    isTitleExpanded: Boolean,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier
    ) {
    AsyncImage(
        model = coverUrl,
        contentDescription = null,
        modifier = Modifier
            .size(300.dp)
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp)
    )

    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )

    Text(
        text = synopsis,
        style = MaterialTheme.typography.titleMedium                   ,
        maxLines = if (isTitleExpanded) Int.MAX_VALUE else 4,
        overflow = if (isTitleExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
        modifier = modifier.clickable { onTitleClick() }
    )
}