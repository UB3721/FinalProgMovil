package com.df.base.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.df.base.model.back.Collection
import androidx.compose.runtime.Composable

@Composable
fun UiCollection(
    collectionList: List<Collection> = listOf(),
    selectedList: List<Collection> = listOf(),
    toggleCollectionSelection: (Collection) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(top = 0.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Collections",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        if (collectionList.isEmpty()) {
            Text(
                text = "No collection found",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 300.dp)
            ) {
                items(collectionList) { collection ->
                    val isSelected = selectedList.contains(collection)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                toggleCollectionSelection(collection)
                            }
                            .background(
                                if (isSelected) Color.LightGray else Color.Transparent
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = collection.collectionName,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}





