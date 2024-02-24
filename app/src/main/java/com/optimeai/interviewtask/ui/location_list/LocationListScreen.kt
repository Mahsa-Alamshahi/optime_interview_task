package com.optimeai.interviewtask.ui.location_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.optimeai.interviewtask.domain.dto.LocationDetails


@Composable
fun LocationListScreenRoute() {

    val viewModel: LocationViewModel = hiltViewModel()

    LocationListScreen(
        viewModel.locationListState,
        viewModel::monitorUserLocation)
}



@Composable
fun LocationListScreen(
    userLocationList: SnapshotStateList<LocationDetails>,
    monitorUserLocation: () -> Unit
) {


    val location = userLocationList.toList()


    LaunchedEffect(key1 = Unit) {
        monitorUserLocation()
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(location) { locationDetails ->
                LocationListItem(location = locationDetails)
            }
        }

    }


}



