package com.optimeai.interviewtask.ui.location_list

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger

@Composable
fun CheckAndRequestLocationPermissions(locationPermissionsGranted: MutableState<Boolean>) {

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val context = LocalContext.current


    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val permissionsGranted =
                permissions.values.reduce { acc, isPermissionGranted ->
                    acc && isPermissionGranted
                }

            locationPermissionsGranted.value = permissionsGranted
            Logger.d("PERR ${locationPermissionsGranted.value}  ***** $permissionsGranted")
        })


    if (
        locationPermissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    ) {
        locationPermissionsGranted.value = true
    } else {
        SideEffect {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }
}