package com.example.composedemo.ui.compose.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.composedemo.R
import com.example.composedemo.data.Photo
import com.example.composedemo.network.*

@Composable
fun ScreenI(navController: NavController = rememberNavController()) {
    val api: Api<List<Photo>> = api()
    val response: Res<List<Photo>>? by api.call(
        url = URL_PHOTOS
    ).compose(scheduleTransformer())
        .subscribeAsState(initial = null)
    val photos: List<Photo> by remember {
        derivedStateOf { response?.data ?: emptyList() }
    }
    LazyColumn {
        item {
            Button(
                onClick = {
                    navController.navigate(route = DESTINATION_II)
                }
            ) {
                Text(text = "Go to II")
            }
        }
        for (photo in photos) {
            item {
                PhotoItem(photo = photo)
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PhotoItem(photo: Photo) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(data = photo.thumbnailUrl),
                contentDescription = null
            )
            Text(text = "${photo.title}")
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Composable
fun ScreenII() {
    Surface {
        Text(text = "ScreenII")
    }
}

@Composable
fun Greeting(name: String) {
    Column {
        var number: Int by remember {
            mutableStateOf(1)
        }
        val api: Api<List<Photo>> = api()
        val photos: List<Photo> by api.call(
            url = URL_PHOTOS
        ).map { it.data ?: emptyList() }
            .compose(scheduleTransformer())
            .subscribeAsState(initial = emptyList())
        val size: Int by remember {
            derivedStateOf { photos.size }
        }
        Text(text = "number = $number $size")
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                number += 1
            },
            enabled = number != 5
        ) {
            Text(text = "Increment")
        }
    }
}

const val DESTINATION_I = "screen_i"
const val DESTINATION_II = "screen_ii"

@Composable
fun RootNavHost(navController: NavHostController = rememberNavController()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            BottomNavigation {
                RootNavItem.values().forEach {
                    BottomNavigationItem(
                        selected = it.route == currentRoute,
                        onClick = {
                            navController.navigate(it.route) {
                                popUpTo(
                                    navController.graph.startDestinationRoute
                                        ?: throw IllegalStateException()
                                ) {
                                    inclusive = false
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = it.icon), contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = DESTINATION_I) {
            composable(route = DESTINATION_I) {
                ScreenI(navController = navController)
            }

            composable(route = DESTINATION_II) {
                ScreenII()
            }
        }
    }
}

interface NavItem {
    val title: String
    val route: String

    @get:DrawableRes
    val icon: Int
}

enum class RootNavItem(
    override val title: String,
    override val route: String,
    override val icon: Int,
) : NavItem {

    DEST_I(
        "SCREEN I",
        DESTINATION_I,
        R.drawable.ic_android_black_24dp
    ),
    DEST_II(
        "SCREEN II",
        DESTINATION_II,
        R.drawable.ic_baseline_10k_24
    ),
}