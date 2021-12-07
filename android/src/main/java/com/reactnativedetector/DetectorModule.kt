package com.reactnativedetector

import android.app.Activity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule
import java.lang.ref.WeakReference


class DetectorModule(val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ScreenshotDetectionListener {
    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(reactContext, this)
    override fun getName(): String {
        return "Detector"
    }

    @ReactMethod
    fun startScreenshotDetection() {
        screenshotDetectionDelegate.startScreenshotDetection()
    }

    @ReactMethod
    fun stopScreenshotDetection() {
        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    @ReactMethod
    fun allowScreenRecording() {
        screenshotDetectionDelegate.allowScreenRecording(WeakReference<Activity>(currentActivity))
    }

    @ReactMethod
    fun blockScreenRecording() {
        screenshotDetectionDelegate.blockScreenRecording(WeakReference<Activity>(currentActivity))
    }

    override fun onScreenCaptured(path: String) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit("UIApplicationUserDidTakeScreenshotNotification", null)
    }

    override fun onScreenCapturedWithDeniedPermission() {
        // Todo: send user notification.
    }
}
