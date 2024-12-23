package com.df.base.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.df.base.R
import com.df.base.model.back.Collection
import com.df.base.model.back.UserManga

@Composable
fun UiListBody(
    title: String,
    mangaList: List<UserManga>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navToDisplay: (UserManga) -> Unit,
    collection: Collection?,
    onDialogActionUserManga: (UserManga) -> Unit = {},
    isCollection: Boolean = false,
    onDialogActionCollection: (Collection) -> Unit = {}
) {
    if (mangaList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.empty_user_manga_list)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(contentPadding)
        ) {
            items(mangaList) { manga ->
                UiItemList(
                    title = title,
                    userManga = manga,
                    navToDisplay = navToDisplay,
                    onDialogActionCollection = onDialogActionCollection,
                    onDialogActionUserManga = onDialogActionUserManga,
                    collection = collection
                )
            }
        }
    }
}