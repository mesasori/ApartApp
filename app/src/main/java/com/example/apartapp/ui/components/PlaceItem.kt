package com.example.apartapp.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private enum class DragAnchors {
    Start,
    Center,
    End,
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaceItem(
    title: String,
    address: String,
    freq: Int,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val defaultActionSize = 80.dp
    val endActionSizePx = with(density) { (defaultActionSize * 2).toPx() }
    val startActionSizePx = with(density) { defaultActionSize.toPx() }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            anchors = DraggableAnchors {
                DragAnchors.Start at -startActionSizePx
                DragAnchors.Center at 0f
                DragAnchors.End at endActionSizePx
            },
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween(),
        )
    }

    Box(
        modifier = Modifier
            .anchoredDraggable(state, Orientation.Horizontal)
            .offset {
                IntOffset(
                    x = state
                        .requireOffset()
                        .roundToInt(), y = 0
                )
            },
    ) {
        Row(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(start = 20.dp, top = 15.dp, end = 30.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = address,
                    fontSize = 16.sp,
                )
            }
            Spacer(Modifier.weight(1f))
            Column {
                Text(
                    text = freq.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview
@Composable
fun PlaceItemPreview() {
    PlaceItem(title = "Title", address = "Address", freq = 7)
}
