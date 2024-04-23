package com.example.apartapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
fun ApartItem(
    modifier: Modifier = Modifier,
    apart: Apart,
) {
    val textList = remember {
        val textList = mutableListOf<String>()
        if (apart.rooms != null)
            textList.add("${apart.rooms} rooms")
        if (apart.floor != null && apart.floorMax != null)
            textList.add("${apart.floor}/${apart.floorMax} floor")
        else if (apart.floor != null)
            textList.add("${apart.floor} floor")
        if (apart.area != null)
            textList.add("${apart.area} m²")
        textList
    }
    Column(modifier) {
        AsyncImage( // TODO handle failing
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(140.dp),
            contentScale = ContentScale.Crop,
            model = apart.imageUrl,
            contentDescription = apart.address
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "${apart.cost} ₽",
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp
        )

        Row(modifier = Modifier.padding(top = 8.dp)) {
            textList.forEachIndexed { i, it ->
                Text(text = it, fontSize = 16.sp)
                if (i != textList.size - 1)
                    Text(
                        modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                        text = "·",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
            }
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = apart.address,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )

        if (apart.metroStation != null)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.metro),
                    contentDescription = "metro"
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = apart.metroStation,
                    fontSize = 14.sp
                )
            }
    }
}

@Preview(showBackground = true)
@Composable
fun ApartItemPreview() {
    ApartItem(
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
