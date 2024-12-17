package com.df.base.ui

import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.df.base.ProjectApplication
import com.df.base.ui.add.AddViewModel
import com.df.base.ui.add.DisplayViewModel
import com.df.base.ui.add.EditViewModel
import com.df.base.ui.collection.CollectionMangaScreen
import com.df.base.ui.collection.CollectionMangaViewModel
import com.df.base.ui.collection.CollectionViewModel
import com.df.base.ui.favorites.FavoritesViewModel
import com.df.base.ui.list.ListViewModel
import com.df.base.ui.login.LoginViewModel
import com.df.base.ui.profile.ProfileViewModel

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
        initializer {
            ProfileViewModel(
                projectApp().container.mangasRepository
            )
        }

        initializer {
            CollectionMangaViewModel(
                this.createSavedStateHandle(),
                projectApp().container.mangasRepository
            )
        }

        initializer {
            LoginViewModel(
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