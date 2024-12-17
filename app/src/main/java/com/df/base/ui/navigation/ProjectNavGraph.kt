package com.df.base.ui.navigation

import android.os.Build
import com.df.base.ui.search.MangaScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
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
import com.df.base.ui.collection.CollectionMangaDestination
import com.df.base.ui.collection.CollectionMangaScreen
import com.df.base.ui.collection.CollectionScreen
import com.df.base.ui.favorites.FavoritesDestination
import com.df.base.ui.favorites.FavoritesScreen
import com.df.base.ui.list.ListDestination
import com.df.base.ui.list.ListScreen
import com.df.base.ui.login.LoginViewModel
import com.df.base.ui.profile.ProfileDestination
import com.df.base.ui.profile.ProfileScreen
import com.df.base.ui.search.SearchDestination
import androidx.hilt.navigation.compose.hiltViewModel
import com.df.base.ui.login.LoginDestination
import com.df.base.ui.login.LoginScreen

@Composable
fun ProjectNavHost(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) SearchDestination.route else LoginDestination.route,
        modifier = modifier
    ) {
        composable(route = LoginDestination.route) {
            LoginScreen(
                onLoginSuccess = {navController.navigate(SearchDestination.route)},
                loginViewModel = loginViewModel
            )
        }

        composable(route = SearchDestination.route) {
            MangaScreen(
                navController = navController,
                loginViewModel = loginViewModel

            )
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

            AddScreen(
                loginViewModel = loginViewModel,
                navController = navController,
                selectedManga = selectedManga,
                navigateBack = {navController.popBackStack()} )
        }
        composable(route = ListDestination.route) {
            ListScreen(
                loginViewModel = loginViewModel,
                navToDisplay = {userManga ->
                    navController.navigate(DisplayDestination.routeWithArgs(userManga))
                },
                navController = navController
            )
        }

        composable(route = CollectionDestination.route) {
            CollectionScreen(
                loginViewModel = loginViewModel,
                navController = navController,
                navToCollectionManga = { navController.navigate("${CollectionMangaDestination.route}/${it}")
                }
            )
        }

        composable(route = FavoritesDestination.route) {
            FavoritesScreen(
                loginViewModel = loginViewModel,
                navToDisplay = {userManga ->
                    navController.navigate(DisplayDestination.routeWithArgs(userManga))
                },
                navController = navController
            )
        }

        composable(route = ProfileDestination.route) {
            ProfileScreen(
                loginViewModel = loginViewModel,
                navController = navController,
                navigateToEdit = { userManga ->
                    navController.navigate(EditDestination.routeWithArgs(userManga))
                }
            )
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
                loginViewModel = loginViewModel,
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
                loginViewModel = loginViewModel,
                navController = navController,
                navigateBack = {
                    navController.popBackStack()
                    navController.popBackStack()
                },
                userManga = userManga
            )
        }

        composable(
            route = CollectionMangaDestination.routeWithArgs,
            arguments = listOf(navArgument(CollectionMangaDestination.collectionIdArg) {
                type = NavType.IntType
            })
        ) {
            CollectionMangaScreen(
                loginViewModel = loginViewModel,
                navToDisplay = {userManga ->
                    navController.navigate(DisplayDestination.routeWithArgs(userManga))
                },
                navController = navController
            )
        }

    }
}