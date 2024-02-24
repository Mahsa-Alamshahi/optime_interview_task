package com.optimeai.interviewtask.ui.location_list

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.optimeai.interviewtask.domain.dto.LocationDetails
import com.orhanobut.logger.Logger


@Composable
fun LocationListScreenRoute() {

    val viewModel: LocationViewModel = hiltViewModel()

    LocationListScreen(
        viewModel.locationListState,
        viewModel::monitorUserLocation)

}


@Composable
fun LocationListScreen(
    list: SnapshotStateList<LocationDetails>,
    monitorUserLocation: () -> Unit
) {


    UserLocationsList(
        userLocationList = list,
        monitorUserLocation
    )
//    else {

//        PermissionsNotGrantedView {

//            CheckAndRequestLocationPermissions(locationPermissionsGranted)
//                locationPermissionLauncher.launch(locationPermissions)

//        }

//    }

}


@Composable
fun UserLocationsList(
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


@Composable
fun PermissionsNotGrantedView(onRequestLocationPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = {
                onRequestLocationPermission()

            },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.List,
                contentDescription = "Add FAB",
                tint = Color.White,
            )
        }
    }
}