package com.df.base.ui.profile

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.model.back.SharedLink
import com.df.base.model.back.UserManga
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.UiListBody
import com.df.base.ui.navigation.NavigationDestination
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.Utils

object ProfileDestination: NavigationDestination {
    override  val route = "profile"
    override  val titleRes = R.string.profile
    override  val icon = Icons.Default.Person
}

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val profileUiState by profileViewModel.profileUiState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserData()
        profileViewModel.fetchSharedLinkList()
        profileViewModel.fetchUserStatistics()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) { innerPadding ->
        ProfileBody(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                ),
            userStats = profileUiState.userStats,
            username = profileUiState.user.userName + "#" + profileUiState.user.userId,
            userMangaList = profileUiState.userMangaList,
            navController = navController,
            setUserMangaStatus = { status, manga ->
                profileViewModel.setUserMangaReadingStatus(status, manga)
            }
        )
    }
}

@Composable
fun ProfileBody(
    username: String,
    userStats: UserStats,
    userMangaList: List<UserManga>,
    navController: NavController,
    setUserMangaStatus: (String, UserManga) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier.padding(vertical = 30.dp)
        )

        if (userStats.onHold != 0 && userStats.dropped != 0 && userStats.reading != 0 && userStats.completed != 0) {
            StatisticalPie(userStats = userStats)
        } else {
            Text(
                text = "Add mangas to see your pie chart",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        InboxProfile(
            userMangaList = userMangaList,
            navController = navController,
            setUserMangaStatus = setUserMangaStatus,
            deleteLink = {}
        )

    }
}

@Composable
fun StatisticalPie(
    userStats: UserStats
) {
    val context = LocalContext.current

    Utils.init(context)

    val colors = listOf(
        MaterialTheme.colorScheme.primary.toArgb(),
        MaterialTheme.colorScheme.secondary.toArgb(),
        MaterialTheme.colorScheme.tertiary.toArgb(),
        MaterialTheme.colorScheme.error.toArgb()
    )

    val onBackground = MaterialTheme.colorScheme.onBackground.toArgb()
    val onPrimary = MaterialTheme.colorScheme.onPrimary.toArgb()

    val pieEntries = listOf(
        PieEntry(userStats.reading.toFloat(), stringResource(R.string.reading)),
        PieEntry(userStats.completed.toFloat(), stringResource(R.string.completed)),
        PieEntry(userStats.onHold.toFloat(), stringResource(R.string.on_hold)),
        PieEntry(userStats.dropped.toFloat(), stringResource(R.string.dropped))
    )

    val pieData = remember(pieEntries, colors) {
        PieData(PieDataSet(pieEntries, "").apply {
            setColors(colors)
            valueTextSize = 24f
            valueTextColor = onPrimary
            setDrawValues(true)
        }).apply {
            setValueFormatter(object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            })
        }
    }

    AndroidView(
        factory = {
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                this.legend.apply {
                    textColor = onBackground
                    textSize = 18f
                    isWordWrapEnabled = true
                    xEntrySpace = 8f
                    yEntrySpace = 4f

                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    orientation = Legend.LegendOrientation.HORIZONTAL
                }
                setDrawEntryLabels(false)
            }
        },
        update = { pieChart ->
            pieChart.data = pieData
            pieChart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}


@Composable
fun InboxProfile(
    modifier: Modifier = Modifier,
    userMangaList: List<UserManga>,
    navController: NavController,
    setUserMangaStatus: (String, UserManga) -> Unit = { _, _ -> },
    deleteLink: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Inbox",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 32.dp, bottom = 16.dp, top = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Log.d("test", userMangaList.toString())
//            UiListBody(
//                mangaList = userMangaList,
//                contentPadding = PaddingValues(16.dp),
//                navController = navController,
//                setUserMangaStatus = setUserMangaStatus
//            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { deleteLink() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete link",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

