package com.example.apartapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.apartapp.ui.theme.ApartTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ApartsScreen(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = bottomBar,
    ) {
        Text("Aparts")
    }
}


@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun ApartsScreenPreview() {
    ApartTheme {
        ApartsScreen(
            bottomBar = {},
        )
    }
}
