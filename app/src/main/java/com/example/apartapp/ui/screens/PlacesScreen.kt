package com.example.apartapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apartapp.ui.components.PlaceItem
import com.example.apartapp.ui.theme.ApartTheme
import com.example.apartapp.ui.viewmodels.Place


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlacesScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddPlaces: () -> Unit,
    bottomBar: @Composable () -> Unit,
    placeItems: List<Place>,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddPlaces) {
                Icon(Icons.Filled.Add, "add place")
            }
        },
        bottomBar = bottomBar,
    ) {
        LazyColumn {
            items(placeItems) { place ->
                PlaceItem(
                    modifier = Modifier.padding(top = 20.dp),
                    title = place.name,
                    address = place.address,
                    freq = place.frequency,
                )
            }
            item { Spacer(modifier = Modifier.padding(40.dp)) }
        }
    }
}


@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun PlacesScreenPreview() {
    ApartTheme {
        PlacesScreen(
            bottomBar = {},
            onNavigateToAddPlaces = {},
            placeItems = listOf<Place>(
                Place(name = "Title0", address = "Address0", frequency = 1),
                Place(name = "Title1", address = "Address1", frequency = 2),
                Place(name = "Title2", address = "Address2", frequency = 3),
            ),
        )
    }
}
