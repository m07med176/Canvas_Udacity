package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent

    /// DownLoading
    private lateinit var url: String
    private lateinit var title: String
    private lateinit var downloadManager: DownloadManager
    private var downloadStatus = ""
    private var downloadID: Long = 0

    companion object {
        /// Link Resources
        private const val GlIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        /// Notification Channel
        private const val CHANNEL_ID = "channelId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        custom_button.setOnClickListener { download()  }

        // Receiver Registration
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    // region Receiver
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadID));
                if (cursor.moveToFirst()) {
                    val status =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadStatus = "success"
                        custom_button.buttonState = ButtonState.Completed
                    } else {
                        downloadStatus = "failed"
                    }
                } else {
                    downloadStatus = "failed"
                }
                sendCompletedNotification(title, downloadStatus)
            }
        }
    }
    // endregion Receiver

    // region Download
    private fun download() {
        when (radioGroup.checkedRadioButtonId) {
            R.id.glide -> {
                url = GlIDE_URL
                title = getString(R.string.glide)
                startDownloading()
            }
            R.id.loadApp -> {
                url = LOAD_APP_URL
                title = getString(R.string.loadApp)
                startDownloading()
            }
            R.id.retrofit -> {
                url = RETROFIT_URL
                title = getString(R.string.retrofit)
                startDownloading()
            }
            else -> Toast.makeText(this, "please select the file to download", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun startDownloading() {
        custom_button.buttonState = ButtonState.Clicked
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }
    // endregion Download

    // region Notification
    @SuppressLint("StringFormatInvalid")
    private fun sendCompletedNotification(filename: String, status: String) {
        createChannel(CHANNEL_ID, "completedChannel")
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra("filename", filename)
        intent.putExtra("status", status)
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(resources.getString(R.string.notification_description, title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .setChannelId(CHANNEL_ID)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(applicationContext).notify(1, builder.build())
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Description"

            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // endregion Notification
}
