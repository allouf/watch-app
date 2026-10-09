package com.incentives.prototype.minigameT1

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.NoteFile
import com.incentives.prototype.R
import com.incentives.prototype.Repository
import com.incentives.prototype.SendMessage
import com.incentives.prototype.Utilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Base64
import java.util.regex.Pattern

class MessageProcessingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    // Get the instance of the repository
    private val repository = Repository.getInstance(applicationContext as Application)

    public fun CoroutineScope.FullsendToMobile(context: Context) {
        val datapath = "/allouf_path"
        var jsonData = Gson().toJson(MyTimer.remindersList)
        launch(Dispatchers.IO) {
            SendMessage(datapath, "FullLayoutsData&&"+jsonData, context ).start()
        }
    }
    /*fun String.matchLabels(): String? {
        return MyTimer.labels.firstOrNull { this.contains("#$it") }
    }*/

    override suspend fun doWork(): Result {
        // Extract the encoded message from the inputData
        val messageEnc = inputData.getString(KEY_MESSAGE_ENC) ?: return Result.failure()

        val message = try {
            decodeBase64(messageEnc)
        } catch (e: IllegalArgumentException) {
            // Log the error or handle it as needed
            Log.e("MessageProcessingWorker", "Failed to decode Base64: ${e.message}")
            return Result.failure()
        }

        // Process the decoded message
        return try {
            processDecodedMessage(message)
            Result.success()
        } catch (e: Exception) {
            // Log the error or handle it as needed
            Log.e("MessageProcessingWorker", "Error processing message: ${e.message}")
            Result.failure()
        }
    }

    private suspend fun processDecodedMessage(message: String) {
        if (message.equals("Connect")) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            applicationContext.startActivity(intent)
            MyTimer.vibrate(applicationContext)
        }
        else if (message.startsWith("SetAlgorithm", false)) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            applicationContext.startActivity(intent)
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            withContext(Dispatchers.Main) {
                repository.sendAction("SetAlgorithm", mapOf("splittedMsg" to splittedMsg))
            }
        }
        else if (message.startsWith("SetAvatar", false)) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            applicationContext.startActivity(intent)
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            withContext(Dispatchers.Main) {
                repository.sendAction("SetAvatar", mapOf("splittedMsg" to splittedMsg))
            }
        }
        else if (message != null && message.startsWith("SetTimer", false)) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            applicationContext.startActivity(intent)
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            withContext(Dispatchers.Main) {
                repository.sendAction("SetTimer", mapOf("splittedMsg" to splittedMsg))
            }
        }
        else if (message.startsWith("StartAndPlay")) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            applicationContext.startActivity(intent)
            withContext(Dispatchers.Main) {
                repository.sendAction("StartAndPlay")
            }
        }
        else if (message.startsWith("PauseAndStop")) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            applicationContext.startActivity(intent)
            withContext(Dispatchers.Main) {
                repository.sendAction("PauseAndStop")
            }
        }
        else if (message.startsWith("StopAndReset")) {
            if (!MyTimer.isTimerStopped) {
                MyTimer.totalStopped += 1
                MyTimer.GtotalStopped += 1
                MyTimer.isTimerPaused = false
                MyTimer.isTimerStopped = true
            }
            MyTimer.resetScoreAndReminder()
            MyTimer.ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
            with(MyTimer.settings.edit()) {
                putInt(
                    applicationContext.getString(R.string.MinigameState),
                    EnumStateType.ALL_INACTIVE.ordinal
                )
                apply()
            }
            MyTimer.vibrate(applicationContext)

            MyTimer.remainingSecondsPrimary = MyTimer.primaryTimerDurString

            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "RESET_UPDATE_TIMER")
            }
            applicationContext.startActivity(intent)
            //LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent("ACTION_RESET_UPDATE_TIMER"))
        }
        else if (message.startsWith("ShowReminder")) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "ACTION_SHOW_REMINDER")
            }
            applicationContext.startActivity(intent)
            MyTimer.doShow = true
            //LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent("ACTION_SHOW_REMINDER"))
        }
        else if (message.startsWith("Continue")) {
            val intent = Intent(applicationContext, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "CONTINUE_EXIT_REMINDER")
            }
            applicationContext.startActivity(intent)
            MyTimer.doContinue = true
            //LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent("ACTION_CONTINUE_EXIT_REMINDER"))
        }
        else if (message.equals("FullGetLayouts")) {
            GlobalScope.FullsendToMobile(applicationContext)
        }

        else if (message.startsWith("DisableLayout", false)) {
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            if (splittedMsg.size > 1) {
                val layoutId: Int = splittedMsg[1].toInt()

                if(layoutId>=0 && layoutId<11){
                    MyTimer.remindersList[layoutId].DisableInList = true
                    val sharedPreferences = applicationContext.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean(
                            applicationContext.getString(Utilities.ListOfRemindersActive[layoutId]),
                            true
                        )//save reminder times
                        apply()
                    }
                }
            }
        }
        else if (message.startsWith("DeleteLayout", false)) {
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            if (splittedMsg.size > 1) {
                val layoutId: Int = splittedMsg[1].toInt()

                if(layoutId>=0 && layoutId<11){
                    val sharedPreferences = applicationContext.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                    MyTimer.remindersList[layoutId].DisableInList = true
                    val targetLayout = Utilities.ListOfReminderTextviewIds[layoutId]
                    with(sharedPreferences.edit()) {
                        putInt(
                            applicationContext.getString(Utilities.ListOfScreensIds[layoutId]),
                            1
                        )
                        putBoolean(
                            applicationContext.getString(Utilities.ListOfRemindersActive[layoutId]),
                            true
                        )
                        apply()
                    }

                    val currScrnsCount = MyTimer.remindersList[layoutId].DisplayScreens.size
                    MyTimer.remindersList[layoutId].resetScreens()


                    for (i in 1..currScrnsCount-1){
                        with(sharedPreferences.edit()) {
                            remove(
                                "screen"+layoutId+i
                            )
                            putString(
                                "screen"+layoutId+0,
                                "Default Text"
                            )
                            apply()
                        }
                    }
                }
            }
        }
        else if (message.startsWith("EnableLayout", false)) {
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            if (splittedMsg.size > 1) {
                val layoutId: Int = splittedMsg[1].toInt()

                if(layoutId>=0 && layoutId<11){
                    MyTimer.remindersList[layoutId].DisableInList = false
                    val sharedPreferences = applicationContext.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean(
                            applicationContext.getString(Utilities.ListOfRemindersActive[layoutId]),
                            false
                        )//save reminder times
                        apply()
                    }
                }
            }
        }
        else if (message.startsWith("AddNewScreen", false)) {
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            if (splittedMsg.size > 1) {
                val layoutId: Int = splittedMsg[1].toInt()
                if(layoutId>=0 && layoutId<11){
                    val currScrnsCount = MyTimer.remindersList[layoutId].DisplayScreens.size
                    var currScrnsLst = MyTimer.remindersList[layoutId].DisplayScreens
                    val newData = "Added New Screen "+((currScrnsCount+1).toString())
                    var n: NoteFile = NoteFile(newData)
                    currScrnsLst.add(n)
                    MyTimer.remindersList[layoutId].DisplayScreens = currScrnsLst

                    val sharedPreferences = applicationContext.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString(
                            "screen"+layoutId+currScrnsCount,
                            newData
                        )
                        putInt(
                            applicationContext.getString(Utilities.ListOfScreensIds[layoutId]),
                            currScrnsCount+1
                        )
                        apply()
                    }
                }
            }
        }
        else if (message.startsWith("RemoveLastScreen", false)) {
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            if (splittedMsg.size > 1) {
                val layoutId: Int = splittedMsg[1].toInt()
                if(layoutId>=0 && layoutId<11){
                    val currScrnsCount = MyTimer.remindersList[layoutId].DisplayScreens.size
                    var currScrnsLst = MyTimer.remindersList[layoutId].DisplayScreens
                    currScrnsLst.removeAt(currScrnsLst.size - 1)
                    MyTimer.remindersList[layoutId].DisplayScreens = currScrnsLst
                    val newCount = currScrnsCount-1

                    val sharedPreferences = applicationContext.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        remove(
                            "screen"+layoutId+newCount
                        )
                        putInt(
                            applicationContext.getString(Utilities.ListOfScreensIds[layoutId]),
                            newCount
                        )
                        apply()
                    }
                }
            }
        }
        else if (message.startsWith("SetScreenLayout", false)) {
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            if (splittedMsg.size > 1) {
                val layoutId: Int = splittedMsg[1].toInt()
                val screenId: Int = splittedMsg[2].toInt()
                val newData: String = splittedMsg[3]

                val sharedPreferences = applicationContext.getSharedPreferences("MinigamePref", MODE_PRIVATE)

                //val scrId = screenId - 1
                with(sharedPreferences.edit()) {
                    putString(
                        "screen" + layoutId + screenId,
                        newData
                    ) //save reminder times
                    apply()
                }

                MyTimer.remindersList[layoutId].DisplayScreens[screenId].Description = newData
            }
        }
        else if (message.startsWith("SetLayout", false)) {
            val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
            if (splittedMsg.size > 1) {
                val layoutId: Int = splittedMsg[1].toInt()
                val newData: String = splittedMsg[2]
                val targetLayout = Utilities.ListOfReminderTextviewIds[layoutId]
                val sharedPreferences =
                    applicationContext.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString(
                        applicationContext.getString(targetLayout),
                        newData
                    )//save reminder times
                    apply()
                }
            }
        }
    }

    /**
     * Attempts to decode a Base64 encoded string, trying both standard and URL-safe decoding.
     * @param encodedString The Base64 encoded string.
     * @return The decoded string.
     * @throws IllegalArgumentException If the string cannot be decoded.
     */
    private fun decodeBase64(encodedString: String): String {
        return try {
            // Try standard decoding
            String(Base64.getDecoder().decode(encodedString), Charsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            // Fallback to URL-safe decoding if standard decoding fails
            try {
                String(Base64.getUrlDecoder().decode(encodedString), Charsets.UTF_8)
            } catch (e: IllegalArgumentException) {
                // Rethrow exception if both decodings fail
                throw IllegalArgumentException("Failed to decode Base64 string, illegal character encountered.")
            }
        }
    }
    companion object {
        const val KEY_MESSAGE_ENC = "key_message_enc"
    }
}