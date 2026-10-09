package com.incentives.prototype

import android.app.Activity

import android.os.Bundle

import android.app.Application.ActivityLifecycleCallbacks
import android.util.Log


class MyLifecycleHandler : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        ++resumed
    }

    override fun onActivityPaused(activity: Activity) {
        ++paused
        Log.w("test", "application is in foreground: " + (resumed > paused))
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityStarted(activity: Activity) {
        ++started
    }

    override fun onActivityStopped(activity: Activity) {
        ++stopped
        Log.w("test", "application is visible: " + (started > stopped))
    }

    companion object {
        // I use four separate variables here. You can, of course, just use two and
        // increment/decrement them instead of using four and incrementing them all.
        private var resumed = 0
        private var paused = 0
        private var started = 0
        private var stopped = 0
        val isApplicationVisible: Boolean
            get() = started > stopped

        val resumedC: Int
            get() = resumed

        val pausedC: Int
            get() = paused

        val startedC: Int
            get() = started

        val stoppedC: Int
            get() = stopped

        val isApplicationInForeground: Boolean
            get() = resumed > paused


    // If you want a static function you can use to check if your application is
        // foreground/background, you can use the following:
        /*
    // Replace the four variables above with these four
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    // And these two public static functions
    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }
    */
    }
}