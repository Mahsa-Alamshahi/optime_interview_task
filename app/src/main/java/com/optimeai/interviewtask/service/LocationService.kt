package com.optimeai.interviewtask.service

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.optimeai.interviewtask.data.data_source.LocationDataSource
import com.optimeai.interviewtask.domain.dto.LocationDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationService : Service() {

    private val LOCATION_NOTIFICATION_CHANNEL_ID = "POSITION"
    private val LOCATION_NOTIFICATION_ID = 1

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var locationDataSource: LocationDataSource


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Location"
            val descriptionText = "Geting current user location"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(LOCATION_NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        sendNotification(null)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start()
        return super.onStartCommand(intent, flags, startId)
    }


    private fun start() {
        serviceScope.launch {
            locationDataSource.getUserLocation().collect { locationDetails ->
                sendNotification(locationDetails)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }


    override fun onBind(p0: Intent?): IBinder? = null


    private fun sendNotification(location: LocationDetails?) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, LOCATION_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_menu_mylocation)
            .setContentTitle("location")
            .setContentText("Latitude: ${location?.longitude ?: ""}     Longitude: ${location?.latitude ?: ""}")
            .setAutoCancel(true)
//            .setContentIntent(
//                PendingIntent.getActivity(
//                    this,
//                    0,
//                    Intent(this, MainActivity::class.java),
//                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//                )
//            )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(LOCATION_NOTIFICATION_ID, builder.build())
        } else {
            startForeground(
                LOCATION_NOTIFICATION_ID, builder.build(), FOREGROUND_SERVICE_TYPE_LOCATION
            )
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(LOCATION_NOTIFICATION_ID, builder.build())
    }
}