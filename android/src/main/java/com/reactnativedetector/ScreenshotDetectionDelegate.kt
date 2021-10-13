package com.reactnativedetector

import android.Manifest
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import androidx.core.content.ContextCompat
import android.database.Cursor
import android.util.Log
import android.view.WindowManager
import java.lang.Exception
import java.lang.ref.WeakReference


class ScreenshotDetectionDelegate(val context: Context, val listener: ScreenshotDetectionListener) {
  lateinit var contentObserver: ContentObserver

  var isListening = false

  fun startScreenshotDetection() {
    contentObserver = object : ContentObserver(Handler()) {
      override fun onChange(selfChange: Boolean, uri: Uri) {
        super.onChange(selfChange, uri)
        if (isReadExternalStoragePermissionGranted()) {
          val path = getFilePathFromContentResolver(context, uri)
          if (isScreenshotPath(path)) {
            onScreenCaptured(path!!)
          }
        } else {
          onScreenCapturedWithDeniedPermission()
        }
      }
    }

    context.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      true,
      contentObserver)
    isListening = true
  }

  fun stopScreenshotDetection() {
    context.getContentResolver().unregisterContentObserver(contentObserver)
    isListening = false
  }

  private fun onScreenCaptured(path: String) {
    listener.onScreenCaptured(path)
  }

  private fun onScreenCapturedWithDeniedPermission() {
    listener.onScreenCapturedWithDeniedPermission()
  }

  fun blockScreenRecording(activityWeakReference: WeakReference<Activity>) {
    activityWeakReference.get()?.runOnUiThread {
      try {
        activityWeakReference.get()?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
      } catch (e: Exception) {
        // Nothing to do here.
      }
    }
  }

  fun allowScreenRecording(activityWeakReference: WeakReference<Activity>) {
    activityWeakReference.get()?.runOnUiThread {
      try {
        activityWeakReference.get()?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
      } catch (e: Exception) {
        // Nothing to do here.
      }
    }
  }

  private fun isScreenshotPath(path: String?): Boolean {
    return path != null && path.toLowerCase().contains("screenshots")
  }

  private fun getFilePathFromContentResolver(context: Context, uri: Uri): String? {
    try {

      val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA), null, null, null)
      if (cursor != null && cursor.moveToFirst()) {
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return path
      }
    } catch (e: Exception) {

    }
    return null
  }

  private fun isReadExternalStoragePermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }
}

interface ScreenshotDetectionListener {
  fun onScreenCaptured(path: String)
  fun onScreenCapturedWithDeniedPermission()
}
