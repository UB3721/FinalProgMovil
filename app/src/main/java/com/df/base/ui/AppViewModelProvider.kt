package com.df.base.ui

import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.df.base.ProjectApplication
import com.df.base.ui.add.AddViewModel
import com.df.base.ui.add.DisplayViewModel
import com.df.base.ui.add.EditViewModel
import com.df.base.ui.collection.CollectionViewModel
import com.df.base.ui.favorites.FavoritesViewModel
import com.df.base.ui.list.ListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddViewModel(
                projectApp().container.mangasRepository
            )
        }
        initializer {
            ListViewModel(
                projectApp().container.mangasRepository
            )
        }
        initializer {
            DisplayViewModel(
                projectApp().container.mangasRepository
            )
        }
        initializer {
            EditViewModel(
                projectApp().container.mangasRepository
            )
        }
        initializer {
            CollectionViewModel(
                projectApp().container.mangasRepository
            )
        }
        initializer {
            FavoritesViewModel(
                projectApp().container.mangasRepository
            )
        }
    }
}

/**
 * Extension function to query for [Application] object and return an instance of
 * [InventoryApplication].
 */
fun CreationExtras.projectApp(): ProjectApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ProjectApplication)