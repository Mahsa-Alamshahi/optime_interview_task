package com.optimeai.interviewtask.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.optimeai.interviewtask.service.LocationService
import com.optimeai.interviewtask.ui.location_list.LocationListScreenRoute
import com.optimeai.interviewtask.ui.location_list.PermissionRequestDialog
import com.optimeai.interviewtask.ui.theme.OptimeInterviewTaskTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OptimeInterviewTaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val showHome = remember { mutableStateOf(false) }


                    val showRationalDialog = remember { mutableStateOf(false) }
                    if (showRationalDialog.value) {

                        PermissionRequestDialog(
                            onDismissRequest = { showRationalDialog.value = false },
                            onConfirmation = {
                                showRationalDialog.value = false
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent, null)

                            },
                            dialogTitle = "Permission Needed!",
                            dialogText = "The location is important for this app. Please grant the permission.",
                            icon = Icons.Default.Warning
                        )
                    }


                    val locationPermissionState =
                        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
                    val backGroundlocationPermissionState =
                        rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    val notificationPermissionState =
                        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

                    val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        showRationalDialog.value = !isGranted
                    }


                    val requestBackgroundPermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            showRationalDialog.value = false
                            if (!notificationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
                                showRationalDialog.value = true

                            } else {
                                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }

                        }
                    }


                    val requestPermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            showHome.value = true
                            showRationalDialog.value = false
                            requestBackgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            if (!backGroundlocationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
                                showRationalDialog.value = true

                            } else {
                                requestBackgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            }

                        } else {
                            showRationalDialog.value = true
                        }
                    }


                    LifecycleEventEffect(Lifecycle.Event.ON_START) {
                        if (!locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale) {
                            showRationalDialog.value = true

                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }

                    }

                    if (showHome.value) {
                        LocationListScreenRoute()
                    }

                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        checkServicePermissions()
    }


    override fun onResume() {
        super.onResume()
        stopService(Intent(this, LocationService::class.java))
    }


    private fun checkServicePermissions() {
        var notificationPermission = false
        var backgroundLocationPermission = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission =
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            backgroundLocationPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (notificationPermission && backgroundLocationPermission) {
            startForegroundService(Intent(this, LocationService::class.java))
        }
    }

}