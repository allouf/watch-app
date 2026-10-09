package com.incentives.prototype

import androidx.appcompat.app.AppCompatActivity

class ResumableActivityDelegate(private val resumableActivity: IResumableActivity) {
    fun onStart() {
        resumableActivity.activityResumed()
    }

    fun onStop() {
        resumableActivity.activityPaused()
    }
}