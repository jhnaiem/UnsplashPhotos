package com.example.unsplashphotos.data.repository

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import com.example.unsplashphotos.R
import timber.log.Timber
import java.io.File

class DownloaderUtils(private val context: Context) {

    var downloadStatusListener: ((Int, String) -> Unit)? = null

    @SuppressLint("Range")
    fun downloadPhoto(url: String, photoId: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) {
            directory.mkdirs()
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(photoId)
                .setDescription(context.getString(R.string.downlaoding))
                .setDestinationInExternalPublicDir(directory.toString(), "$photoId.jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        var finishDownload = false
        while (!finishDownload) {
            val cursor: Cursor = downloadManager.query(query)
            try {
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_FAILED -> {
                            finishDownload = true
                        }
                        DownloadManager.STATUS_PAUSED -> {}
                        DownloadManager.STATUS_PENDING -> {}
                        DownloadManager.STATUS_RUNNING -> {}
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            downloadStatusListener?.invoke(status, directory.toString())
                            finishDownload = true
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Timber.tag("Error").e(e.toString())
            } finally {
                cursor.close()
            }
        }
    }
}
