package com.incentives.prototype

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import com.incentives.prototype.minigameT1.MessageProcessingWorker
import com.incentives.prototype.minigameT1.MinigameT1
import java.util.Base64

class MessageBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.incentives.prototype.MINIGAME") {
            val messageEnc = intent.getStringExtra("message") ?: return

            // Use KEY_MESSAGE_ENC from MessageProcessingWorker's companion object
            val inputData = Data.Builder()
                .putString(MessageProcessingWorker.KEY_MESSAGE_ENC, messageEnc)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<MessageProcessingWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
/*import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.incentives.prototype.minigameT1.MinigameT1
import com.incentives.prototype.minigameT1.MyTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.util.Base64 as JavaBase64
import java.util.regex.Pattern
import android.util.Base64
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MessageBroadcastReceiver : BroadcastReceiver() {

    public fun CoroutineScope.FullsendToMobile(context: Context) {
        val datapath = "/allouf_path"
        var jsonData = Gson().toJson(MyTimer.remindersList)
        launch(Dispatchers.IO) {
            SendMessage(datapath, "FullLayoutsData&&"+jsonData, context ).start()
        }
    }

    fun String.matchLabels(): String? {
        return MyTimer.labels.firstOrNull { this.contains("#$it") }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == MinigameT1.ACTION_MY_BROADCAST) {
            val messageEnc = intent.getStringExtra("message")
            val utf8Bytes = JavaBase64.getDecoder().decode(messageEnc)
            val message = utf8Bytes.toString(Charsets.UTF_8)
            if (message != null) {
                if (message.equals("FullGetLayouts")) {
                    GlobalScope.FullsendToMobile(context)
                }
                else if (message.equals("Connect")) {
                    MyTimer.vibrate(context)
                    val i = Intent(context, MinigameT1::class.java).apply {
                        // Always set FLAG_ACTIVITY_NEW_TASK when starting an activity from a BroadcastReceiver
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(i)
                }
                else if (message != null && message.startsWith("SetTimer", false)) {
                    val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
                    if (splittedMsg.size > 1) {
                        val timer: Int = splittedMsg[1].toInt()
                        val vibration: Int = splittedMsg[2].toInt()
                        MyTimer.isTimerPaused = false
                        MyTimer.isTimerStopped = true
                        MyTimer.ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
                        MyTimer.remainingSecondsPrimary = timer
                        MyTimer.primaryTimerDurString = timer
                        MyTimer.totalRemindersString = vibration

                        /*with(MyTimer.settings.edit()) {

                            putInt(
                                MyTimer.context.getString(R.string.totalRemindersString),
                                vibration
                            )
                            putInt(
                                MyTimer.context.getString(R.string.TimerRemainingSec_Primary),
                                MyTimer.remainingSecondsPrimary
                            )
                        }*/

                        MyTimer.initReminderTimes(context, false)
                        val reminderTimesString = Gson().toJson(MyTimer.reminderTimes)
                        with(MyTimer.settings.edit()) {
                            putInt(
                                context.getString(R.string.primaryTimerDurString),
                                timer
                            )

                            putInt(
                                context.getString(R.string.MinigameState),
                                EnumStateType.ALL_INACTIVE.ordinal
                            )
                            putString(
                                context.getString(R.string.ReminderTimes),
                                reminderTimesString
                            )
                            apply()
                        }
                        MyTimer.vibrate(context)
                        val i = Intent(context, MinigameT1::class.java)
                        if (!MyApplication.isActivityVisible) {
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(i)
                        } else {
                            i.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            context.startActivity(i)
                            // Send a local broadcast
                            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("ACTION_UPDATE_TIMER"))
                        }
                    }
                }
                else if (message.startsWith("StartAndPlay")) {
                    if (!MyTimer.isTimerPaused && MyTimer.isTimerStopped)
                    {
                        MyTimer.totalRun += 1
                        MyTimer.GtotalRun += 1
                        Log.d("MyTag", "Timer starting from beginning")
                        MyTimer.InitializeAndStartTimer(
                            context,
                            EnumAlarmTypes.valueOf("MinigameT1"),
                            EnumStateType.PRMR_TMR_RNG_ONLY.ordinal,
                            MyTimer.settings.getInt(
                                context.getString(R.string.primaryTimerDurString),
                                context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)
                            )
                        )
                    }
                    else
                    {
                        MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)
                        with(MyTimer.settings.edit()) {
                            putInt(
                                context.getString(R.string.MinigameState),
                                EnumStateType.PRMR_TMR_RNG_ONLY.ordinal
                            )
                            apply()
                        }
                    }
                    MyTimer.isTimerPaused = false
                    MyTimer.isTimerStopped = false
                    MyTimer.vibrate(context)
                    val i = Intent(context, MinigameT1::class.java).apply {
                        // Always set FLAG_ACTIVITY_NEW_TASK when starting an activity from a BroadcastReceiver
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(i)
                }
                else if (message.startsWith("PauseAndStop")) {
                    if(!MyApplication.isActivityVisible)
                        MyTimer.ResumeTimerBasedOnPrefAfterPause(context, EnumAlarmTypes.valueOf("MinigameT1"))
                    if (!MyTimer.isTimerPaused && !MyTimer.remainingSecondsPrimary.equals(-1) && !MyTimer.isTimerStopped) {
                        MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_PSD.ordinal)
                        MyTimer.isTimerPaused = true
                        MyTimer.isTimerStopped = false
                        MyTimer.vibrate(context)
                        with(MyTimer.settings.edit()) {
                            putInt(
                                context.getString(R.string.MinigameState),
                                EnumStateType.PRMR_TMR_PSD.ordinal
                            )
                            apply()
                        }
                        val i = Intent(context, MinigameT1::class.java)
                        if(!MyApplication.isActivityVisible) {
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        else {
                            i.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        }
                        context.startActivity(i)
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
                            context.getString(R.string.MinigameState),
                            EnumStateType.ALL_INACTIVE.ordinal
                        )
                        apply()
                    }
                    MyTimer.vibrate(context)

                    MyTimer.remainingSecondsPrimary = MyTimer.primaryTimerDurString

                    val i = Intent(context, MinigameT1::class.java).apply {
                        // Always set FLAG_ACTIVITY_NEW_TASK when starting an activity from a BroadcastReceiver
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(i)

                    //MyTimer.reset()
                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("ACTION_RESET_UPDATE_TIMER"))
                }
                else if (message.startsWith("ShowReminder")) {
                    if(MyApplication.isActivityVisible)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("ACTION_SHOW_REMINDER"))
                    else {
                        MyTimer.doShow = true
                        val i = Intent(context, MinigameT1::class.java).apply {
                            // Always set FLAG_ACTIVITY_NEW_TASK when starting an activity from a BroadcastReceiver
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(i)
                    }
                }
                else if (message.startsWith("Continue")) {
                    if(MyApplication.isActivityVisible)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("ACTION_CONTINUE_EXIT_REMINDER"))
                    else {
                        MyTimer.doContinue = true
                        val i = Intent(context, MinigameT1::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(i)
                    }
                }
                else if (message.startsWith("SetAlgorithm", false)) {
                    val i = Intent(context, MinigameT1::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(i)

                    val showDialogIntent = Intent("com.incentives.prototype.SHOW_DIALOG")
                    LocalBroadcastManager.getInstance(context).sendBroadcast(showDialogIntent)

                    val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
                    if (splittedMsg.size > 1) {
                        if(MyTimer.currentState != EnumStateType.ALL_INACTIVE.ordinal){
                            MyTimer.isTimerPaused = false
                            MyTimer.isTimerStopped = true
                            MyTimer.resetScoreAndReminder()
                            MyTimer.ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
                            MyTimer.totalStopped += 1
                            MyTimer.GtotalStopped += 1
                        }
                        val gson = Gson()

                        GlobalScope.launch(Dispatchers.IO) {
                            MyTimer.vibrate(context)
                            val algo = gson.fromJson(splittedMsg[1],AlgorithmFile::class.java)

                            //val labelPattern = Pattern.compile("#(EXPLORING|CREATING|EXPRESSING|LEARNING)\\b")
                            //val matcher = labelPattern.matcher(algo.Description)
                            val label = algo.Description.matchLabels()
                            if (label != null) {
                                //val label = matcher.group(1)
                                MyTimer.currLable = label
                                MyTimer.algoDescription = algo.Description
                                MyTimer.algoTitle = algo.Name
                                //MyTimer.AIImageTex = Base64.decode(algo.AIImageTexBase64, Base64.DEFAULT)
                                MyTimer.LevelNo = algo.LevelNo
                                MyTimer.CurrentProgress = algo.CurrentProgress
                                MyTimer.CurrentAnalytics = algo.CurrentAnalytics



                                val algosIds = AlgosIds.getInstance(context)

                                // Check if the received algo ID already exists in the list
                                if (algosIds.containsId(algo.Id)) {
                                    // The ID exists, handle the logic here as needed
                                    MyTimer.flatAlgoData(algo,context)
                                    Log.d("Receiver", "Received algo with ID ${algo.Id}, which already exists")
                                }
                                else {
                                    // The ID does not exist, add it to the list
                                    algosIds.addId(algo.Id)
                                    MyTimer.flatAlgoData(algo,context)
                                    MyTimer.totalRun = 0
                                    MyTimer.totalCompleted = 0
                                    MyTimer.totalStopped = 0
                                    MyTimer.totalRemindersRecieved = 0
                                    MyTimer.totalAlgos += 1
                                    MyTimer.totalTime = 0
                                    Log.d("Receiver", "Received new algo with ID ${algo.Id}")
                                }

                            }
                        }
                    }
                    //EventBus.getDefault().postSticky(CustomShowDialogEvent(R.id.fragment_minigame_t1_s3))
                }
                else if (message.startsWith("SetAvatar", false)) {
                    // Intent to start an Activity, adding flags directly
                    val i = Intent(context, MinigameT1::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(i)

                    val showDialogIntent = Intent("com.incentives.prototype.SHOW_DIALOG")
                    LocalBroadcastManager.getInstance(context).sendBroadcast(showDialogIntent)

                    val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
                    if (splittedMsg.size > 1) {
                        // Acquire the Application scope from context (ensure context isn't null)
                        (context.applicationContext as? MyApplication)?.applicationScope?.launch(Dispatchers.IO) {
                            try {
                                MyTimer.vibrate(context)
                                val avatarProfile = Gson().fromJson(splittedMsg[1], AvatarProfile::class.java)

                                MyTimer.avatarProfile = avatarProfile
                                // This is to update any shared preferences
                                val avatarProfileString = Gson().toJson(avatarProfile)

                                // Assuming `settings` is accessible and thread-safe
                                with(context.getSharedPreferences("MinigamePref", Context.MODE_PRIVATE).edit()) {
                                    putString(context.getString(R.string.AvatarProfile), avatarProfileString)
                                    apply()
                                }

                                withContext(Dispatchers.Main) {
                                    // If there's any UI operation directly based on this data change
                                    // Optional based on need -- EventBus, LiveData notification, etc.
                                }
                            } catch (e: Exception) {
                                Log.e("AppScope", "Error processing set avatar: ${e.localizedMessage}")
                                // Handle error, perhaps send a broadcast to notify the failure
                            }
                        }
                    }
                }
                else if (message.startsWith("DisableLayout", false)) {
                    val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
                    if (splittedMsg.size > 1) {
                        val layoutId: Int = splittedMsg[1].toInt()

                        if(layoutId>=0 && layoutId<11){
                            MyTimer.remindersList[layoutId].DisableInList = true
                            val sharedPreferences = context.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putBoolean(
                                    context.getString(Utilities.ListOfRemindersActive[layoutId]),
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
                            val sharedPreferences = context.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                            MyTimer.remindersList[layoutId].DisableInList = true
                            val targetLayout = Utilities.ListOfReminderTextviewIds[layoutId]
                            with(sharedPreferences.edit()) {
                                putInt(
                                    context.getString(Utilities.ListOfScreensIds[layoutId]),
                                    1
                                )
                                putBoolean(
                                    context.getString(Utilities.ListOfRemindersActive[layoutId]),
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
                            val sharedPreferences = context.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putBoolean(
                                    context.getString(Utilities.ListOfRemindersActive[layoutId]),
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
                            var n:NoteFile = NoteFile(newData)
                            currScrnsLst.add(n)
                            MyTimer.remindersList[layoutId].DisplayScreens = currScrnsLst

                            val sharedPreferences = context.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString(
                                    "screen"+layoutId+currScrnsCount,
                                    newData
                                )
                                putInt(
                                    context.getString(Utilities.ListOfScreensIds[layoutId]),
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

                            val sharedPreferences = context.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                remove(
                                    "screen"+layoutId+newCount
                                )
                                putInt(
                                    context.getString(Utilities.ListOfScreensIds[layoutId]),
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

                        val sharedPreferences = context.getSharedPreferences("MinigamePref", MODE_PRIVATE)

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
                            context.getSharedPreferences("MinigamePref", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString(
                                context.getString(targetLayout),
                                newData
                            )//save reminder times
                            apply()
                        }
                    }
                }
                else if (message.startsWith("AddNewEmptyLayout", false)) {
                    val splittedMsg: List<String> = message.split(Pattern.compile("&&"))
                    if (splittedMsg.size > 1) {
                        val layoutId: Int = splittedMsg[1].toInt()
                        if(layoutId>=0 && layoutId<11){
                        }
                    }
                }
            }
        }
    }
}*/