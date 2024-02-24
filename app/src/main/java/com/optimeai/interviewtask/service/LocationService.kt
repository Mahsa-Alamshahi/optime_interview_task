package com.optimeai.interviewtask.service

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.optimeai.interviewtask.data.data_source.LocationDataSource
import com.optimeai.interviewtask.domain.dto.LocationDetails
import com.optimeai.interviewtask.ui.MainActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationService : Service() {

    val NOTIFICATION_CHANNEL_ID = "POSITION"

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var locationDataSource: LocationDataSource


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Location"
            val descriptionText = "LatLng"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        sendNotification(null)
//notification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start()
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun start() {
        serviceScope.launch {
            locationDataSource.fetchUpdates().collect { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                sendNotification(location)
            }
        }
    }


    override fun onBind(p0: Intent?): IBinder? = null


    private fun sendNotification(location: LocationDetails?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "Location Channel"
            val descriptionText = "Latitude:   Longitude:  "
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(mChannel)
        }

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_ID
            ) // these are the three things a NotificationCompat.Builder object requires at a minimum
                .setSmallIcon(R.drawable.ic_menu_mylocation)
                .setContentTitle("location")
                .setContentText("Latitude: ${location?.longitude ?: ""}     Longitude: ${location?.latitude ?: ""}") // notification will be dismissed when tapped
                .setAutoCancel(true) // tapping notification will open MainActivity
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, MainActivity::class.java),
                        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(1, builder.build())
        } else {
            startForeground(
                1, builder.build(),
                FOREGROUND_SERVICE_TYPE_LOCATION
            )
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}