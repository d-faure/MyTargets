package de.dreier.mytargets.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Simple permission utility to replace PermissionsDispatcher
 * Provides the same functionality without annotation processing
 */
object PermissionUtils {
    
    const val REQUEST_CAMERA = 1001
    const val REQUEST_STORAGE = 1002
    const val REQUEST_WRITE_STORAGE = 1003
    
    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
    
    fun requestPermission(fragment: Fragment, permission: String, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }
    
    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
    
    fun shouldShowRationale(fragment: Fragment, permission: String): Boolean {
        return fragment.shouldShowRequestPermissionRationale(permission)
    }
    
    fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
    
    // Camera permission helpers
    fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.CAMERA)
    }
    
    fun requestCameraPermission(activity: Activity) {
        requestPermission(activity, Manifest.permission.CAMERA, REQUEST_CAMERA)
    }
    
    fun requestCameraPermission(fragment: Fragment) {
        requestPermission(fragment, Manifest.permission.CAMERA, REQUEST_CAMERA)
    }
    
    // Storage permission helpers
    fun hasStoragePermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    
    fun requestStoragePermission(activity: Activity) {
        requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_STORAGE)
    }
    
    fun requestStoragePermission(fragment: Fragment) {
        requestPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_STORAGE)
    }
    
    // Write storage permission helpers
    fun hasWriteStoragePermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    
    fun requestWriteStoragePermission(activity: Activity) {
        requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_STORAGE)
    }
    
    fun requestWriteStoragePermission(fragment: Fragment) {
        requestPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_STORAGE)
    }
}

