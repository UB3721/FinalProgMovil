package com.df.base.ui.profile

import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.df.base.BottomNavigationBar
import com.df.base.R
import com.df.base.model.back.SharedLink
import com.df.base.model.back.User
import com.df.base.model.back.UserManga
import com.df.base.ui.AppViewModelProvider
import com.df.base.ui.CommonDialog
import com.df.base.ui.MangaAction
import com.df.base.ui.ShowWarningAlert
import com.df.base.ui.add.toMangaDetails
import com.df.base.ui.login.LoginViewModel
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
    loginViewModel: LoginViewModel,
    navController: NavController,
    navigateToLogin: () -> Unit,
    navigateToEdit: (UserManga) -> Unit,
    profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val profileUiState by profileViewModel.profileUiState.collectAsState()

    when (val state = profileUiState.state) {
        is ProfileUiState.State.Error -> {
            ShowWarningAlert(state.message)
        }
        else -> {}
    }

    LaunchedEffect(Unit) {
        profileViewModel.setUser(
            User(
                userId = loginViewModel.user.value?.userId!!,
                userName = loginViewModel.user.value?.userName!!
            )
        )
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
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = innerPadding.calculateBottomPadding()
                ),
            userStats = profileUiState.userStats,
            username = profileUiState.user.userName + "#" + profileUiState.user.userId,
            sharedLinkList = profileUiState.userSharedLinkList,
            userMangaList = profileUiState.userMangaList,
            navigateToEdit = navigateToEdit,
            deleteLink = { sharedLink ->
                profileViewModel.removeSharedLink(sharedLink)
                         },
            logout = {
                loginViewModel.logout()
                navigateToLogin()
            },
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
    setUserMangaStatus: (String, UserManga) -> Unit = { _, _ -> },
    sharedLinkList: List<SharedLink>,
    navigateToEdit: (UserManga) -> Unit,
    deleteLink: (SharedLink) -> Unit,
    logout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { logout() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout"
                )
            }
        }

        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        if (userStats.onHold != 0 || userStats.dropped != 0 || userStats.reading != 0 || userStats.completed != 0) {
            StatisticalPie(userStats = userStats)
        } else {
            Text(
                text = stringResource(R.string.no_data_available),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        InboxProfile(
            userMangaList = userMangaList,
            setUserMangaStatus = setUserMangaStatus,
            deleteLink = deleteLink,
            navigateToEdit = navigateToEdit,
            sharedLinkList = sharedLinkList
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

    val allPieEntries = listOf(
        PieEntry(userStats.reading.toFloat(), stringResource(R.string.reading)),
        PieEntry(userStats.completed.toFloat(), stringResource(R.string.completed)),
        PieEntry(userStats.onHold.toFloat(), stringResource(R.string.on_hold)),
        PieEntry(userStats.dropped.toFloat(), stringResource(R.string.dropped))
    )

    val filteredPieEntries = allPieEntries.filter { it.value > 0 }

    if (filteredPieEntries.isEmpty()) {
        Text(
            text = stringResource(R.string.no_data_available),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        return
    }

    val pieData = remember(filteredPieEntries, colors) {
        PieData(PieDataSet(filteredPieEntries, "").apply {
            setColors(colors.take(filteredPieEntries.size))
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
    sharedLinkList: List<SharedLink>,
    navigateToEdit: (UserManga) -> Unit,
    setUserMangaStatus: (String, UserManga) -> Unit = { _, _ -> },
    deleteLink: (SharedLink) -> Unit
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Inbox",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sharedLinkList.zip(userMangaList)) { (sharedLink, userManga) ->
                UserMangaItem(
                    sender = "${sharedLink.sender.userName}#${sharedLink.sender.userId}",
                    userManga = userManga,
                    sharedLink = sharedLink,
                    onDelete = deleteLink,
                    setUserMangaStatus = setUserMangaStatus,
                    navigateToEdit = navigateToEdit
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserMangaItem(
    sender: String,
    sharedLink: SharedLink,
    userManga: UserManga,
    setUserMangaStatus: (String, UserManga) -> Unit,
    navigateToEdit: (UserManga) -> Unit,
    onDelete: (SharedLink) -> Unit
) {
    var expanded by rememberSaveable() { mutableStateOf(false) }
    var showDialog by rememberSaveable() { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = { showDialog = true }
            )
    ) {
        Text(
            text = "From: $sender",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = userManga.coverUrl,
                contentDescription = "Cover image for ${userManga.userTitle}",
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = userManga.userTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                (if (expanded) userManga.synopsis else (userManga.synopsis?.take(100) ?: "") + "...")?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable { expanded = !expanded }
                    )
                }
            }

            IconButton(
                onClick = {
                    onDelete(sharedLink)
                          },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete UserManga",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDialog) {
        setUserMangaStatus(stringResource(R.string.reading), userManga)
        CommonDialog(
            title = stringResource(R.string.dialog_edit_manga),
            mangaAction = MangaAction.Edit(
                userManga.toMangaDetails()
            ),
            showDialog = showDialog,
            onShowDialogChanged = { showDialog = false },
            navigateToEdit = { navigateToEdit(userManga) }
        )
    }
}

