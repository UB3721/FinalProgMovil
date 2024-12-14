package com.df.base.ui.navigation

import android.os.Build
import com.df.base.ui.search.MangaScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.df.base.model.back.UserManga
import com.df.base.ui.SelectedManga
import com.df.base.ui.add.AddDestination
import com.df.base.ui.add.AddScreen
import com.df.base.ui.add.DisplayDestination
import com.df.base.ui.add.DisplayScreen
import com.df.base.ui.add.EditDestination
import com.df.base.ui.add.EditScreen
import com.df.base.ui.collection.CollectionDestination
import com.df.base.ui.collection.CollectionScreen
import com.df.base.ui.favorites.FavoritesDestination
import com.df.base.ui.favorites.FavoritesScreen
import com.df.base.ui.list.ListDestination
import com.df.base.ui.list.ListScreen
import com.df.base.ui.profile.ProfileDestination
import com.df.base.ui.profile.ProfileScreen
import com.df.base.ui.search.SearchDestination

@Composable
fun ProjectNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SearchDestination.route,
        modifier = modifier
    ) {
        composable(route = SearchDestination.route) {
            MangaScreen(navController = navController)
        }
        composable(
            route = "${AddDestination.route}/{${AddDestination.selectedMangaArg}}",
            arguments = listOf(
                navArgument(AddDestination.selectedMangaArg) {
                    type = nonNullableNavType(SelectedManga.serializer())
                }
            )
        ) {  backStackEntry ->
            val selectedManga = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                backStackEntry.arguments?.getParcelable(
                    AddDestination.selectedMangaArg,
                    SelectedManga::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                backStackEntry.arguments?.getParcelable(AddDestination.selectedMangaArg)
            } ?: error("SelectedManga argument is missing.")

            AddScreen(selectedManga = selectedManga, navController = navController, navigateBack = {navController.popBackStack()} )
        }
        composable(route = ListDestination.route) {
            ListScreen(
                navController = navController
            )
        }

        composable(route = CollectionDestination.route) {
            CollectionScreen(navController = navController)
        }

        composable(route = FavoritesDestination.route) {
            FavoritesScreen(navController = navController)
        }

        composable(route = ProfileDestination.route) {
            ProfileScreen(navController = navController)
        }

        composable(
            route = "${DisplayDestination.route}/{${DisplayDestination.USERMANGA}}",
            arguments = listOf(
                navArgument(DisplayDestination.USERMANGA) {
                    type = nonNullableNavType(UserManga.serializer())
                }
            )
        ) { backStackEntry ->
            val userManga = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                backStackEntry.arguments?.getParcelable(
                        DisplayDestination.USERMANGA,
                    UserManga::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                backStackEntry.arguments?.getParcelable(DisplayDestination.USERMANGA)
            } ?: error("UserManga argument is missing.")

            DisplayScreen(
                userManga = userManga,
                navigateToEdit = {navController.navigate(EditDestination.routeWithArgs(userManga))},
                navController = navController
            )
        }

        composable(
            route = "${EditDestination.route}/{${EditDestination.USERMANGA}}",
            arguments = listOf(
                navArgument(EditDestination.USERMANGA) {
                    type = nonNullableNavType(UserManga.serializer())
                }
            )
        ) { backStackEntry ->
            val userManga = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                backStackEntry.arguments?.getParcelable(
                    EditDestination.USERMANGA,
                    UserManga::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                backStackEntry.arguments?.getParcelable(EditDestination.USERMANGA)
            } ?: error("UserManga argument is missing.")

            EditScreen(
                navController = navController,
                navigateBack = {
                    navController.popBackStack()
                    navController.popBackStack()
                },
                userManga = userManga
            )
        }

    }
}