package com.example.apartapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apartapp.ui.theme.ApartTheme


@Composable
private fun FloatingButtonsContainer(
    onBack: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        content()
        FloatingActionButton(
            modifier = Modifier
                .padding(15.dp, 15.dp)
                .size(50.dp)
                .align(Alignment.TopStart),
            shape = CircleShape,
            onClick = onBack
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "go back")
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 15.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .size(50.dp),
                onClick = onZoomIn
            ) {
                Icon(Icons.Filled.Add, "zoom in")
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(50.dp),
                onClick = onZoomOut
            ) {
                Icon(Icons.Filled.Remove, "zoom out")
            }
        }
    }
}


@Composable
private fun SheetContent() {
    Column(Modifier.padding(10.dp)) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = {
                Text("Search")
            },
            value = "",
            onValueChange = {}
        )
    }
}


@Composable
@ExperimentalMaterial3Api
fun AddPlacesScreen(
    modifier: Modifier = Modifier,
    onNavigateToParent: () -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = { SheetContent() },
        modifier = modifier,
    ) {
        FloatingButtonsContainer(
            onBack = onNavigateToParent,
            onZoomIn = { /*TODO*/ },
            onZoomOut = { /*TODO*/ }) {
            Box(    // TODO there should be map
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxSize()
            ) {}
        }
    }
}


@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
@ExperimentalMaterial3Api
fun MapScreenPreview() {
    ApartTheme {
        AddPlacesScreen(onNavigateToParent = {})
    }
}