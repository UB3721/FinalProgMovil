package com.df.base.ui.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.df.base.R

@Composable
fun UiFavorite(
    isFavorite: Boolean,
    onFavoriteChanged: () -> Unit = {},
    isEditable: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor =
        if (isFavorite) Color(0xFFFFD700)
        else Color(0xFF616161)

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription =
            if (isFavorite) stringResource(R.string.favorite)
            else stringResource(R.string.not_favorite),
            modifier = modifier
                .clickable(enabled = isEditable) {
                    if (isEditable) onFavoriteChanged()
                }
                .size(45.dp),
            tint = iconColor
        )
    }
}
