package com.optimeai.interviewtask.ui

import android.Manifest
import android.app.AlertDialog
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
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.optimeai.interviewtask.service.LocationService
import com.optimeai.interviewtask.ui.location_list.LocationListScreenRoute
import com.optimeai.interviewtask.ui.theme.OptimeInterviewTaskTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val BACKGROUND_LOCATION_PERMISSION_CODE: Int = 1000
    private val permissionsRequired = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )

    private val askPermissions = arrayListOf<String>()

    val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->


//                    viewModel.inputs.onTurnOnNotificationsClicked(granted)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OptimeInterviewTaskTheme {


                var allPermissionGranted by remember {
                    mutableStateOf(false)
                }



                val permissionsLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
                        allPermissionGranted = permissionsMap.get(Manifest.permission.ACCESS_FINE_LOCATION) ?: false &&  permissionsMap.get(Manifest.permission.ACCESS_COARSE_LOCATION) ?: false
                    }

                for (permission in permissionsRequired) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        askPermissions.add(permission)
                    }
                }

                allPermissionGranted = askPermissions.isEmpty()


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    if (!allPermissionGranted) {
                        LaunchedEffect(key1 = Unit) {
                            permissionsLauncher.launch(askPermissions.toTypedArray())
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

                                    } else {

                                        pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }


                            } else {

                                askPermissionForBackgroundUsage()

//                                SideEffect {
////                                    permissionsLauncher.launch(arrayOf( Manifest.permission.ACCESS_BACKGROUND_LOCATION))
//
//                                }

//                                }
                            }
                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//
//                            } else {
//
//                                pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                            }
//                        }

                        LocationListScreenRoute()
                    }


                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStop() {
        super.onStop()
        checkPermission()

//        startForegroundService(Intent(this, LocationService::class.java))
    }


    override fun onResume() {
        super.onResume()
        stopService(Intent(this, LocationService::class.java))
    }


    private fun checkPermission() {


        var arePermissionsGranted = true


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if ((ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)
//                    (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                ) {
                    arePermissionsGranted = false
//                    startForegroundService(Intent(this, LocationService::class.java))
                    // Background Location Permission is granted so do your work here
                }
            }

//                startForegroundService(Intent(this, LocationService::class.java))


        } else {
            arePermissionsGranted = false
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            arePermissionsGranted = false
        }

        if (arePermissionsGranted) {
            startForegroundService(Intent(this, LocationService::class.java))
        }
    }


    private fun askPermissionForBackgroundUsage() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                .setPositiveButton(
                    "OK"
                ) { dialog, which ->
//                    ActivityCompat.requestPermissions(
//                        this@MainActivity,
//                        arrayOf<String>(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
//                        BACKGROUND_LOCATION_PERMISSION_CODE
//                    )
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialog, which ->
                    // User declined for Background Location Permission.
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_PERMISSION_CODE
            )
        }
    }

}
