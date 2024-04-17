package com.example.apartapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apartapp.ui.theme.ApartTheme
import kotlinx.coroutines.launch


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
                modifier = Modifier,
                onClick = onZoomIn
            ) {
                Icon(Icons.Filled.Add, "zoom in")
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(top = 16.dp),
                onClick = onZoomOut
            ) {
                Icon(Icons.Filled.Remove, "zoom out")
            }
        }
    }
}


@Composable
private fun SearchSheetContent(
    modifier: Modifier,
    onTextFieldFocusChanged: (Boolean) -> Unit,
) {
    var textInput by remember { mutableStateOf("") }
    Column(modifier) {
        TextField(
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .onFocusChanged { onTextFieldFocusChanged(it.isFocused) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = {
                Text("Search")
            },
            value = textInput,
            onValueChange = { textInput = it }
        )
    }
}


@Composable
@ExperimentalMaterial3Api
fun AddPlacesScreen(
    onNavigateToParent: () -> Unit,
) {
    var sheetSwipeEnabled by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val coroutineScope = rememberCoroutineScope()
    val scaffoldSheetState = rememberBottomSheetScaffoldState(
        SheetState(
            skipPartiallyExpanded = false,
            density = LocalDensity.current,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true,
            confirmValueChange = { sheetValue ->
                sheetSwipeEnabled = sheetValue == SheetValue.Expanded
                true
            }
        )
    )
    val clearFocusModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { focusManager.clearFocus() })
    }
    BottomSheetScaffold(
        sheetContent = {
            SearchSheetContent(
                modifier = clearFocusModifier
                    .padding(
                        start = 15.dp, end = 15.dp,
                        top = if (sheetSwipeEnabled) 0.dp else 15.dp
                    )
                    .heightIn(min = screenHeight - 120.dp),
                onTextFieldFocusChanged = {
                    if (it) {
                        coroutineScope.launch {
                            scaffoldSheetState.bottomSheetState.expand()
                        }
                    }
                })
        },
        modifier = clearFocusModifier,
        sheetDragHandle = {
            if (sheetSwipeEnabled) BottomSheetDefaults.DragHandle() else Unit
        },
        sheetSwipeEnabled = sheetSwipeEnabled,
        scaffoldState = scaffoldSheetState,
        sheetPeekHeight = 125.dp
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