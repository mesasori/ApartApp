package com.example.apartapp.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apartapp.ui.components.Navbar
import com.example.apartapp.ui.components.NavbarButton
import com.example.apartapp.ui.theme.ApartTheme
import com.example.apartapp.ui.viewmodels.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apartapp.ui.viewmodels.NavbarItemEnum


/** Handles Screen changes and defines navbar for every screen that requires it*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {
    val selectedNavItem by mainViewModel.navItems.collectAsState()
    val placeItems by mainViewModel.placeItems.collectAsState()

    val navController = rememberNavController()

    val bottomBar: @Composable () -> Unit = {
        Navbar {
            NavbarItemEnum.entries.forEach { entry ->
                NavbarButton(
                    onClick = {
                        mainViewModel.setSelectedNavItem(entry)
                        navController.navigate(
                            when (entry) {
                                NavbarItemEnum.PLACES -> "places"
                                NavbarItemEnum.APARTS -> "aparts"
                            }
                        )
                    },
                    text = entry.itemName,
                    selected = (entry == selectedNavItem)
                )
            }
        }
    }

    NavHost(navController = navController, startDestination = "places") {
        composable(
            route = "places",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            PlacesScreen(
                placeItemsState = mainViewModel.placeItems,
                bottomBar = bottomBar,
                onNavigateToAddPlaces = {
                    navController.navigate("places/add")
                },
            )
        }
        composable(
            route = "places/add",
            enterTransition = { EnterTransition.None },
            exitTransition = {ExitTransition.None}
        ) {
            AddPlacesScreen(
                onNavigateToParent = {
                    navController.navigate("places")
                }
            )
        }
        composable(
            route = "aparts",
            enterTransition = { EnterTransition.None },
            exitTransition = {ExitTransition.None}
        ) {
            ApartsScreen(
                bottomBar = bottomBar
            )
        }
    }
}


@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun MainScreenPreview() {
    ApartTheme {
        MainScreen()
    }
}