package com.example.apartapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apartapp.ui.components.ApartItem
import com.example.apartapp.ui.theme.ApartTheme
import com.example.apartapp.ui.viewmodels.Apart
import com.example.apartapp.ui.viewmodels.ApartsViewModel


@Composable
private fun SheetContent(
    modifier: Modifier,
    aparts: List<Apart>
) {
    LazyColumn(modifier) {
        item {
            Row(modifier = Modifier
                .padding(start=20.dp, bottom = 20.dp, top = 0.dp)) {
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
        items(aparts) {apart ->
            ApartItem(
                modifier = Modifier.padding(20.dp),
                apart = apart
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ApartsScreen(
    apartsViewModel: ApartsViewModel = viewModel(),
    bottomBar: @Composable () -> Unit,
) {
    val aparts by apartsViewModel.aparts.collectAsState()

    val scaffoldSheetState = rememberBottomSheetScaffoldState(
        SheetState(
            skipPartiallyExpanded = false,
            density = LocalDensity.current,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true,
        )
    )
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    BottomSheetScaffold(
        modifier = Modifier,
        sheetContent = {
            SheetContent(
                modifier = Modifier
                    .heightIn(max=screenHeight-150.dp),
                aparts = aparts,
            )
        },
        scaffoldState = scaffoldSheetState,
        sheetPeekHeight = 100.dp,
    ) {
        Box(    // TODO there should be map
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                bottomBar()
            }
        }
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
