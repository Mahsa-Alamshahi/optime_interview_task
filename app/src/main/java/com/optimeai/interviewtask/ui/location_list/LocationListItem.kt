package com.optimeai.interviewtask.ui.location_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.optimeai.interviewtask.domain.dto.LocationDetails
import kotlin.random.Random


@Composable
fun LocationListItem(location: LocationDetails) {


    val randomColor = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))


    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(1.dp, Color.Gray),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {


            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(randomColor)
                    .padding(8.dp)

            ) {
                Text(
                    text = location.dateTime,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                )
            }

            Text(
                text = "Latitude:  ${location.latitude}",
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            Text(
                text = "Longitude:  ${location.longitude}",
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

    }

}


@Preview(showSystemUi = true)
@Composable
fun PreviewLocationListItem() {
    LocationListItem(location = LocationDetails(0.523322122, 0.9522222, "2024:"))
}