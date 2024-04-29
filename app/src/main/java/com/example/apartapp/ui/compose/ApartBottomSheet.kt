package com.example.apartapp.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.apartapp.R
import com.example.apartapp.ui.viewmodels.Apart

@Composable
private fun CoverImage(imageUrl: String) {
    AsyncImage( // TODO handle failing
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 18.dp),
        contentScale = ContentScale.Crop,
        model = imageUrl,
        contentDescription = "apart",
    )
}

@Composable
private fun Header(address: String, metroStation: String?) {
    Column {
        Text(
            text = address,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
        )
        if (metroStation != null)
            Row(
                modifier = Modifier.padding(top=4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.metro),
                    contentDescription = "metro"
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = metroStation,
                    fontSize = 14.sp
                )
            }
    }
}

@Composable
private fun SectionTitle(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    )
}

@Composable
private fun GallerySection() {
    Column(
        modifier = Modifier.padding(top=24.dp),
    ) {
        SectionTitle(text = "Gallery")

    }
}

@Composable
private fun DescriptionSection(descriptionText: String) {
    Column(
        modifier = Modifier.padding(top=24.dp),
    ) {
        SectionTitle(text = "Description")
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = descriptionText,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun AboutSection(apart: Apart) {
    val aboutData = mutableListOf<Pair<String, String>>()
    if (apart.rooms != null)
        aboutData.add(Pair("Rooms", apart.rooms.toString()))
    if (apart.floor != null && apart.floorMax != null)
        aboutData.add(Pair("Floor", "${apart.floor}/${apart.floorMax}"))
    else if (apart.floor != null)
        aboutData.add(Pair("Floor", apart.floor.toString()))
    if (apart.area != null)
        aboutData.add(Pair("Area", "${apart.area} m²"))

    Column(
        modifier = Modifier.padding(top=24.dp),
    ) {
        SectionTitle(
            modifier = Modifier.padding(bottom=2.dp),
            text = "About"
        )
        aboutData.forEach {
            Row(modifier = Modifier.padding(top=12.dp)) {
                Text(
                    text = it.first,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = it.second,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartBottomSheet(apart: Apart) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { /*TODO*/ },
    ) {
        Column {
            Header(address = apart.address, metroStation = apart.address)
            CoverImage(imageUrl = apart.imageUrl)
            if (apart.description != null)
                DescriptionSection(descriptionText = apart.description)
            AboutSection(apart = apart)

            Button(
                modifier = Modifier
                    //.height(40.dp)
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .clip(RoundedCornerShape(4.dp)),
                onClick = { /*TODO*/ }
            ) {
                Text("Check it out")
            }
        }
    }
}


@Composable
@Preview(
    showSystemUi = true
)
fun ApartBottomSheetPreview() {
    ApartBottomSheet(
        apart = Apart(
            imageUrl = "https://shorturl.at/aow09",
            cost = 20000,
            address = "Москва, САО, р-н Хорошевский, Хорошевское ш., 25Ак1",
            area = 85,
            rooms = 2,
            floor = 10,
            floorMax = 15,
        )
    )
}