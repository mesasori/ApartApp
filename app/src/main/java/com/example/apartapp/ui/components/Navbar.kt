package com.example.apartapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.NavbarButton(onClick: () -> Unit, text: String, selected: Boolean) {
    Button(
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.weight(1f),
        enabled = true,
        onClick = onClick,
    ) {
        Text(text)
    }
}

@Composable
fun Navbar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(    // !TODO тут откуда-то падинг по вертикале лишний
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(6.dp)
    ) {
        content()
    }
}


@Preview
@Composable
fun NavbarTogglePreview() {
    Navbar {
        NavbarButton(
            onClick = {},
            text = "Apart",
            selected = true
        )
        NavbarButton(
            onClick = {},
            text = "Places",
            selected = false
        )
    }
}