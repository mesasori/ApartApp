package com.example.apartapp.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apartapp.ui.components.Navbar
import com.example.apartapp.ui.components.NavbarButton
import com.example.apartapp.ui.viewmodels.MainViewModel
import com.example.apartapp.ui.viewmodels.NavbarItemEnum

private const val PlacesRoute = "places"
private const val ApartsRoute = "aparts"
private const val AddPlacesRoute = "places/add"

/** Handles Screen changes and defines navbar for every screen that requires it*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val selectedNavItem by mainViewModel.navItems.collectAsState()

    val navController = rememberNavController()

    val bottomBar: @Composable () -> Unit = {
        Navbar {
            NavbarItemEnum.entries.forEach { entry ->
                NavbarButton(
                    onClick = {
                        mainViewModel.setSelectedNavItem(entry)
                        navController.navigate(
                            when (entry) {
                                NavbarItemEnum.PLACES -> PlacesRoute
                                NavbarItemEnum.APARTS -> ApartsRoute
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
            route = PlacesRoute,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            PlacesScreen(
                placeItemsState = mainViewModel.placeItems,
                bottomBar = bottomBar,
                onNavigateToAddPlaces = {
                    navController.navigate(AddPlacesRoute)
                },
            )
        }
        composable(
            route = AddPlacesRoute,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {

            AddPlacesScreen(
                onNavigateToParent = {
                    navController.navigate(PlacesRoute)
                }
            )
        }
        composable(
            route = ApartsRoute,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
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
    MainScreen()
}
