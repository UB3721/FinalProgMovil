package com.df.base.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.df.base.R

@Composable
fun ShowWarningAlert(message: String) {
    var showDialog by rememberSaveable { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = stringResource(R.string.warning))
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

