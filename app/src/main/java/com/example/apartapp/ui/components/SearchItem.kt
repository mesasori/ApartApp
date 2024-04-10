package com.example.apartapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchItem(
    modifier: Modifier = Modifier,
    name: String ?= null,
    address: String,
) {
    Column(modifier = modifier) {
        if (name != null) {
            Text(modifier = Modifier.padding(bottom = 2.dp),
                text = name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = address, fontSize = 12.sp, fontWeight = FontWeight.Normal)
        }
        else {
            Text(text = address, fontSize = 12.sp, fontWeight = FontWeight.Normal)
        }
    }
}