package com.example.apartapp.ui.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apartapp.ui.components.ApartItem
import com.example.apartapp.ui.viewmodels.Apart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartsListBottomSheet(
    apartsState: StateFlow<List<Apart>>
) {
    val aparts by apartsState.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { /*TODO*/ },
    ) {
        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, bottom = 20.dp, top = 0.dp)
                ) {
                    Text(
                        text = aparts.size.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = "aparts found in this area",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }
            }
            items(aparts) { apart ->
                ApartItem(
                    modifier = Modifier.padding(20.dp),
                    apart = apart
                )
            }
        }
    }
}


@Composable
@Preview(
    showSystemUi = true,
)
fun ApartsListBottomSheetPreview() {
    ApartsListBottomSheet(
        apartsState = MutableStateFlow(
            listOf(
                Apart(
                    imageUrl = "https://shorturl.at/pFKTY",
                    cost = 200000,
                    address = "Москва, САО, р-н Хорошевский, Хорошевское ш., 25Ак1",
                    rooms = 2,
                    floor = 10,
                    floorMax = 15,
                    area = 85,
                    metroStation = "Полежаевская"
                ),
            )
        )
    )
}