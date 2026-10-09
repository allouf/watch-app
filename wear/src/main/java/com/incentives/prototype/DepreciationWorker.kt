package com.incentives.prototype

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.incentives.prototype.minigameT1.MyTimer

class DepreciationWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result {
        return try {
            depreciateProgressBars()
            Result.success()
        } catch (e: Exception) {
            Log.e("DepreciationWorker", "Error during depreciation", e)
            Result.failure()
        }
    }

    private fun depreciateProgressBars() {
        // Your depreciation logic here
        val avatarProfile: AvatarProfile = MyTimer.avatarProfile

        if (avatarProfile.ProgressBars.isNotEmpty()) {
            val progressBar = avatarProfile.ProgressBars.getOrNull(MyTimer.currentProgressBarIndex)
            progressBar?.let {
                it.ProgressValue = (it.ProgressValue - 10.0f).coerceAtLeast(0.0f)
            }
            MyTimer.avatarProfile = avatarProfile
            MyTimer.updateAvatarProfile(applicationContext, avatarProfile)
            MyTimer.updateAvatarProfileListen(applicationContext, avatarProfile)

            // Increment the index for the next hour, and reset if it exceeds the number of progress bars
            MyTimer.currentProgressBarIndex = (MyTimer.currentProgressBarIndex + 1) % avatarProfile.ProgressBars.size
        }
    }
}