package com.df.base


import com.df.base.ui.search.SearchDestination
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.df.base.ui.collection.CollectionDestination
import com.df.base.ui.favorites.FavoritesDestination
import com.df.base.ui.list.ListDestination
import com.df.base.ui.navigation.ProjectNavHost
import com.df.base.ui.profile.ProfileDestination

@Composable
fun ProjectApp(navController: NavHostController = rememberNavController()) {
    ProjectNavHost(
        navController = navController,
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val bottomBarDestinations = listOf(
        SearchDestination,
        ListDestination,
        CollectionDestination,
        FavoritesDestination,
        ProfileDestination
    )

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomBarDestinations.forEach { destination ->
            val isSelected = currentDestination == destination.route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                    .padding(vertical = 8.dp)
            ) {
                destination.icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = stringResource(destination.titleRes),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = stringResource(destination.titleRes),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

