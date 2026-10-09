package com.incentives.prototype

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PermissionRequestActivity : AppCompatActivity() {

    // Define a request permission launcher that you'll use to handle the permission request callback
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue with the operation that requires the permission.
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                // Implement any action that needed this permission here
                // For example, you might want to proceed with posting a notification
            } else {
                // Explain to the user that the feature is unavailable because the permission is not granted
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            // Finish this activity and return to the previous one
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No UI is needed for this activity, but you can set one if you like
        // setContentView(R.layout.activity_permission_request)

        // Check if the POST_NOTIFICATIONS permission is already granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted; you can proceed with posting notifications
                finish() // Permission is granted, so finish the activity
            } else {
                // Permission is not granted, so request it
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // For OS versions below Android 13, the permission model does not apply to notifications
            finish() // Finish the activity as no action is needed for older versions
        }
    }
}