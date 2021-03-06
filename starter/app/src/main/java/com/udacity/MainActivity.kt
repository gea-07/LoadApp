package com.udacity

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
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    var urlStringID: Int = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Create notification channel that will alert users when download of a resource has completed
        createChannel(
            getString(R.string.load_app_channel_id),
            getString(R.string.load_app_channel_name)
        )

        // Register the download brodcast receiver object that will handle the broadcast completed state
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // When Dowload button is clicked, start the download process
        custom_button.setOnClickListener {
            download()
        }
    }
    // Declare the broadcast receiver that will handle the the download broadcast completed status
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                Log.d("DOWNLOAD", "Download is done!")
                custom_button.buttonState = ButtonState.Completed

                // Find out the status of the download
                var statusString: String = ""
                val query = DownloadManager.Query().setFilterById(id)
                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(
                        DownloadManager.COLUMN_STATUS
                    ))
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL ->
                            statusString = getString(R.string.download_successful)
                        DownloadManager.STATUS_FAILED ->
                            statusString = getString(R.string.download_failed)
                    }
                }

                // Notify the user that the download is complete using notification
                val notificationManager = ContextCompat.getSystemService(
                    application,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(
                    getString(urlStringID),
                    statusString,
                    application)

                // In addition, notify the user using toast
                Toast.makeText(applicationContext,
                    getString(R.string.load_app_channel_message),
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun download() {
        var url: String=""
        // Figure out which URL was selected by the user
        val loadAppGroup = findViewById<RadioGroup>(R.id.radio_load_group)
        when(loadAppGroup.checkedRadioButtonId) {
            R.id.glide_button -> {
                url = GLIDE_URL
                urlStringID = R.string.glide_text
            }
            R.id.udacity_button -> {
                url = UDACITY_URL
                urlStringID = R.string.udacity_text
            }
            R.id.retrofit_button -> {
                url = RETROFIT_URL
                urlStringID = R.string.retrofit_text
            }
        }
        // If the user forgot to select a URL, notify them with a toast message
        if (url.isEmpty()) {
            Toast.makeText(this,
                getString(R.string.choose_message),
                Toast.LENGTH_LONG)
                .show()
            return
        } else { // Start downloading the resource
            Log.d("DOWNLOAD", url)
            // Cancel any previous notifications before starting a new download
            val notificationManager = ContextCompat.getSystemService(
                application,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.cancelNotifications()
            // Set the custom button's state to loading to start animation of this button
            custom_button.buttonState = ButtonState.Loading
            // Create the download request
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            // Enqueue the download request
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download completed"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
