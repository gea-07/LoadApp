package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap

private val NOTIFICATION_ID = 0
const val URL_KEY = "com.udacity.url"
const val DOWNLOAD_STATUS_KEY = "com.udacity.downloadStatus"

// Builds and delivers notification
@SuppressLint("WrongConstant")
fun NotificationManager.sendNotification(messageBody: String, status: String,
                                         applicationContext: Context) {
    // Create the content intent for the notification, which launches the DetailActivity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    // Create pending intent to open this app's MainActivity when the user clicks on the notification
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create the detail activity intent
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(URL_KEY, messageBody)
    detailIntent.putExtra(DOWNLOAD_STATUS_KEY, status)

    // Create another pending intent to open up the app's DetailActivity when the user clicks on Detail button
    val detailPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Add style
    val bitmap = AppCompatResources
        .getDrawable(applicationContext, R.drawable.ic_baseline_cloud_download_24)?.toBitmap()

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(bitmap)
        .bigLargeIcon(null)
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.load_app_channel_id))
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.load_app_channel_message))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setStyle(bigPicStyle)
        .setLargeIcon(bitmap)
        .addAction(
            R.drawable.ic_baseline_cloud_download_24,
            applicationContext.getString(R.string.open_detail),
            detailPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}

// Cancel all notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
