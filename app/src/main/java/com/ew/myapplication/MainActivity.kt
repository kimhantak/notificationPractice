package com.ew.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    private val primaryNotificationID: Int = 1
    private val notificationID: String = "NOTIFICATION"
    private val notificationTitle: String = "alarm"
    private val customIntent: String = "INTENT_CUSTOM_VALUE"
    private val bitmap: Bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
    }

    private val notificationManager: NotificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationChannel: NotificationChannel by lazy {
        NotificationChannel(notificationID, notificationTitle, NotificationManager.IMPORTANCE_HIGH)
    }

    private val broadcastReceiver: BroadcastReceiver by lazy {
        CustomReceiver()
    }

    private val intentFilter: IntentFilter by lazy {
        IntentFilter(customIntent)
    }

    private lateinit var createNotification: Button

    private lateinit var defaultPendingIntent: PendingIntent
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (android.os.Build.VERSION_CODES.O <= android.os.Build.VERSION.SDK_INT) {
            notificationManager.createNotificationChannel(notificationChannel)
        }

        with (notificationChannel) {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.WHITE
        }

        createNotification = findViewById(R.id.create)
        createNotification.setOnClickListener {
            sendNotification()
        }

        this@MainActivity.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun sendNotification(action: Int = 1) {
        val notificationBuilder = getNotificationCompat()
        val defaultIntent = Intent(this, MainActivity::class.java)
        val intent: Intent = Intent(customIntent)
        defaultPendingIntent = PendingIntent.getActivity(this@MainActivity,
                primaryNotificationID,
                defaultIntent,
                PendingIntent.FLAG_ONE_SHOT)
        pendingIntent = PendingIntent.getBroadcast(this@MainActivity,
                primaryNotificationID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        with (notificationBuilder) {
            when (action) {
                1 -> {
                    setContentTitle("hello world!")
                    setContentText("This is notification")
                    setSmallIcon(R.drawable.ic_baseline_airline_seat_recline_extra_24)
                    setContentIntent(defaultPendingIntent)
                    addAction(R.drawable.ic_baseline_airline_seat_recline_extra_24,
                            "Change",
                            pendingIntent)
                }
                0 -> {
                    val bigPictureStyle = NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .setBigContentTitle("hello world!")
                    setStyle(bigPictureStyle)
                    setSmallIcon(R.drawable.ic_baseline_airline_seat_recline_extra_24)
                    setContentIntent(defaultPendingIntent)
                    addAction(R.drawable.ic_baseline_airline_seat_recline_extra_24,
                            "Change",
                            pendingIntent)
                }
                else -> return@with
            }
        }
        notificationManager.notify(primaryNotificationID, notificationBuilder.build())
    }

    private fun getNotificationCompat(): NotificationCompat.Builder {
        val notificationCompat: NotificationCompat.Builder = NotificationCompat.Builder(
                this@MainActivity,
                notificationID)
        with (notificationCompat) {
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setAutoCancel(true)
        }
        return notificationCompat
    }

    inner class CustomReceiver : BroadcastReceiver() {
        override fun onReceive(content: Context?, action: Intent?) {
            sendNotification(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}