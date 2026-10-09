package com.incentives.prototype.minigameT1

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.incentives.prototype.*
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import org.json.JSONArray
import java.lang.Runnable
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlin.math.max

//import org.junit.rules.Timeout.seconds

class MyTimer {
    companion object {
        // Define a unique ID for the missed reminders notification
        private const val MISSED_REMINDERS_NOTIFICATION_ID = 10
        var currentProgressBarIndex = 0
        var avatarProfile: AvatarProfile = AvatarProfile()
        private var listeners = mutableListOf<AvatarProfileUpdateListener>()
        lateinit var settings: SharedPreferences
        lateinit var context: Context
        lateinit var reminderTimes: ReminderTimes
        private lateinit var scores: Scores
        var currentState=EnumStateType.ALL_INACTIVE.ordinal
        var remainingSecondsPrimary:Int=-1
        var MinigameTypeOrdinal: Int = -1
        var lastSeenSecond:Int=-1
        private lateinit var timerPrimary:Runnable
        var totalRemindersString:Int=3
        var primaryTimerDurString:Int=180

        var nodeId: String = ""
        const val COUNT_KEY = "com.example.key.count"
        var selectedSyncNodesIds = arrayListOf<String>()

        var isTimerPaused: Boolean = false
        var isTimerStopped: Boolean = true

        var doContinue: Boolean = false
        var doShow: Boolean = false

        var isSwipedBack: Boolean = false

        var lastRemPagesCount: Int = 0
        var lastRemId: Int = 0
        var lastRemScreenId: Int = 0
        var remindersList = Array(11) {LayoutFile(ArrayList(),true )}
        var readyReminders = ArrayList<Int>()

        val labels: List<String> = listOf("EXPLORING", "CREATING", "EXPRESSING", "LEARNING")
        var selectedType : String = "Thought"

        var currLable: String = "LEARNING"
        var algoDescription: String = "Description"
        var algoTitle: String = "Title"
        var CurrentProgress: List<Int> = arrayListOf()
        var CurrentAnalytics: List<Int> = arrayListOf()

        var totalRun: Int = 0
        var totalCompleted: Int = 0
        var totalStopped: Int = 0
        var totalRemindersRecieved: Int = 0
        var totalTime: Int = 0

        var totalAlgos: Int = 0
        var GtotalRun: Int = 0
        var GtotalCompleted: Int = 0
        var GtotalStopped: Int = 0
        var GtotalTime: Int = 0

        var titleAnswer = ""
        var storyAnswer = ""
        var pageAnswer = ""
        var hintReady: Boolean = false
        val pagesSysMsg ="You are amazing hint writer. You have Generated many hints about sentences, questions, tips. "
        val questionsSysMsg ="You are amazing writer. You have Generated many questions, instructions and tips. if you see a question please generate provoking questions which helps to look differently. Please do not ask for confirmation.  Because the user dont have access to the keyboard on this device. "

        val client = OkHttpClient()

        var currentDisplayScreenStates = ArrayList<HintNoteFileState>()

        fun initializeWithContext(appContext: Context) {
            if(this::context.isInitialized)
                settings = context.getSharedPreferences("MinigamePref", Context.MODE_PRIVATE)
            else
                settings = appContext.getSharedPreferences("MinigamePref", Context.MODE_PRIVATE)
        }

        fun initAvatarProfile(context: Context): Boolean {
            //if(this.isContextInialized()) {
            if (settings.contains(context.getString(R.string.AvatarProfile))) {
                // Avatar profile exists in the shared preferences -> Use it
                val avatarProfileString = settings.getString(
                    context.getString(R.string.AvatarProfile),
                    null
                )
                avatarProfileString?.let {
                    val retrievedProfile = Gson().fromJson(it, AvatarProfile::class.java)
                    avatarProfile = retrievedProfile
                }
            }
            else {
                // Avatar profile does not exist -> Initialize a new default one
                val progressBar1 = ProgressBarUnity("long text Progress1 ", 180f)
                val progressBar2 = ProgressBarUnity("long long text Progress ", 180f)
                val progressBar3 = ProgressBarUnity("long long long text Progress3 ", 180f)

                var defaultProgressBars = ArrayList<ProgressBarUnity>()
                defaultProgressBars.add(progressBar1)
                defaultProgressBars.add(progressBar2)
                defaultProgressBars.add(progressBar3)

                avatarProfile = AvatarProfile("Default", defaultProgressBars)

                // Save it into shared preferences for further launches
                val avatarProfileString = Gson().toJson(avatarProfile)
                with(settings.edit()) {
                    putString(
                        context.getString(R.string.AvatarProfile),
                        avatarProfileString
                    )
                    apply()
                }
            }
            //}

            return true
        }

        @Synchronized
        fun updateAvatarProfile(context: Context, newAvatarProfile: AvatarProfile) {
            val prefs = context.getSharedPreferences("MinigamePref", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            val avatarProfileString = Gson().toJson(newAvatarProfile)
            editor.putString(context.getString(R.string.AvatarProfile), avatarProfileString)
            editor.apply()
        }

        fun initializeReadyReminders() {
            val enabled = getEnabledReminders()
            readyReminders = enabled.shuffled().toMutableList() as ArrayList<Int>
        }

        fun getNextReminderId(): Int {
            if (readyReminders.isEmpty())
                initializeReadyReminders()

            val nextReminderId = readyReminders.removeAt(0)
            return nextReminderId
        }

        fun findLabel(input: String): String? {
            return labels.find { label -> input.contains("#${label}") }
        }

        fun findLabelIndex(input: String): Int {
            for (label in labels.withIndex()) {
                if (input.equals(label)) {
                    return label.index
                }
            }
            return -1
        }

        fun updateCurrentDisplayScreenStates(displayScreens: ArrayList<NoteFile>) {
            currentDisplayScreenStates.clear()
            displayScreens.forEach {
                currentDisplayScreenStates.add(HintNoteFileState(isActive = false, oldHint = ""))
            }
        }

        fun updateNoteFileBooleanState(index: Int, newBoolean: Boolean) {
            if (index >= 0 && index < currentDisplayScreenStates.size) {
                currentDisplayScreenStates[index].isActive = newBoolean
            } else {
                println("Error: Invalid index provided to 'updateNoteFileBooleanState'")
            }
        }

        fun updateNoteFileStringState(index: Int, newString: String) {
            if (index >= 0 && index < currentDisplayScreenStates.size) {
                currentDisplayScreenStates[index].oldHint = newString
            } else {
                println("Error: Invalid index provided to 'updateNoteFileStringState'")
            }
        }

        fun addResponse(response: String,type: String) {
            if(type=="title") {
                titleAnswer = response
            }
            else
                if(type=="story") {
                    storyAnswer = response
                }
                else
                    if(type=="page") {
                        pageAnswer = response
                        hintReady = true
                    }
        }

        fun generateResponse(question: String, sysMsg: String, activity: Activity,type: String) {
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
            val jsonBody = JSONObject()
            try {
                jsonBody.put("model", "gpt-3.5-turbo")
                jsonBody.put(
                    "messages",
                    JSONArray()
                        .put(JSONObject()
                            .put("role", "system")
                            .put("content", sysMsg))
                        .put(JSONObject()
                            .put("role", "user")
                            .put("content", question))
                )
                jsonBody.put("n", 1)
                jsonBody.put("max_tokens", 4000)
                jsonBody.put("temperature", 0.8)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val body = RequestBody.create(JSON, jsonBody.toString())
            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //generateResponse(question, sysMsg, activity,type)
                    activity.runOnUiThread {
                        addResponse(("Failed to load response due to " + e.message),type)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("API",response.toString())
                        var jsonObject: JSONObject? = null
                        try {
                            jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                            activity.runOnUiThread {
                                addResponse(result.trim(), type)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    } else {
                        //generateResponse(question, sysMsg, activity,type)
                        activity.runOnUiThread {
                            addResponse("Failed to load response due to " + response.body.toString(),type)
                        }
                    }
                }
            })
        }

        fun flatAlgoData(algo:AlgorithmFile, context: Context){
            val sl :ArrayList<LayoutFile> = algo.LayoutList
            val slCnt = sl.size
            with(settings.edit())  {
                clear()
                for (i in 0..slCnt-1){
                    val layout = sl[i]
                    val layoutScreensData = layout.DisplayScreens
                    val layoutScreens = layoutScreensData.size

                    putBoolean(
                        context.getString(Utilities.ListOfRemindersActive[i]),
                        false
                    )
                    putInt(
                        context.getString(Utilities.ListOfScreensIds[i]),
                        layoutScreens
                    )

                    for (j in 0 until layoutScreens){
                        putString(
                            "screen"+i+j,
                            layoutScreensData[j].Description
                        )
                    }
                    MyTimer.remindersList[i].DisableInList = false
                    MyTimer.remindersList[i].DisplayScreens = layoutScreensData
                }

                for (k in slCnt..10) {

                    putInt(
                        context.getString(Utilities.ListOfScreensIds[k]),
                        1
                    )
                    putBoolean(
                        context.getString(Utilities.ListOfRemindersActive[k]),
                        true
                    )
                    MyTimer.remindersList[k].DisableInList = true

                    if (MyTimer.remindersList[k].DisplayScreens.isEmpty()) {
                        // Initialize DisplayScreens with a default NoteFile
                        MyTimer.remindersList[k].DisplayScreens.add(NoteFile()) // "Default Text" will be used
                    } else {
                        // If not empty, call resetScreens
                        MyTimer.remindersList[k].resetScreens()
                    }

                }



                putString(
                    context.getString(R.string.currLable),
                    currLable
                )

                putString(
                    context.getString(R.string.algoDescription),
                    algoDescription
                )

                putString(
                    context.getString(R.string.algoTitle),
                    algoTitle
                )


                apply()
            }
        }

        fun initRemindersData(context: Context){
            //if(this.isContextInialized()) {
            for (i in remindersList.indices) {
                remindersList[i].DisableInList = settings.getBoolean(
                    context.getString(Utilities.ListOfRemindersActive[i]),
                    false
                )
                val numScreens = settings.getInt(context.getString(Utilities.ListOfScreensIds[i]), i + 1)

                // Clear existing DisplayScreens before adding new ones
                remindersList[i].DisplayScreens.clear()

                for (j in 0 until numScreens) {
                    val key = "screen$i$j"
                    val description = settings.getString(key, key).toString()
                    Log.d("MyTimer", "Retrieved description for key $key: $description")

                    val noteFile = NoteFile(description)
                    remindersList[i].DisplayScreens.add(noteFile)
                }
            }

            initAvatarProfile(context)

            algoDescription= settings.getString(context.getString(R.string.algoDescription), "Description").toString()
            algoTitle= settings.getString(context.getString(R.string.algoTitle), "Title").toString()
            currLable= settings.getString(context.getString(R.string.currLable), "LEARNING").toString()
            totalRun =settings.getInt(context.getString(R.string.totalRun), 0)
            totalCompleted =settings.getInt(context.getString(R.string.totalCompleted), 0)
            totalStopped =settings.getInt(context.getString(R.string.totalStopped), 0)
            totalRemindersRecieved =settings.getInt(context.getString(R.string.totalRemindersRecieved), 0)
            totalTime =settings.getInt(context.getString(R.string.totalTime), 0)
            totalAlgos =settings.getInt(context.getString(R.string.totalAlgos), 0)
            GtotalRun =settings.getInt(context.getString(R.string.GtotalRun), 0)
            GtotalCompleted =settings.getInt(context.getString(R.string.GtotalCompleted), 0)
            GtotalStopped =settings.getInt(context.getString(R.string.GtotalStopped), 0)
            GtotalTime =settings.getInt(context.getString(R.string.GtotalTime), 0)
            lastRemId=settings.getInt(context.getString(R.string.lastShowenReminder), 0)
            primaryTimerDurString = settings.getInt(context.getString(R.string.primaryTimerDurString),
                context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1))
            totalRemindersString = settings.getInt(context.getString(R.string.totalRemindersString),
                context.resources.getInteger(R.integer.totalRemindersToShow))
            //}
        }

        fun getEnabledReminders():ArrayList<Int>{
            var res:ArrayList<Int> = ArrayList()
            for(i in remindersList.indices){
                if(!remindersList[i].DisableInList)
                    res.add(i)
            }
            return res
        }

        fun getLastRemPages(){
            lastRemPagesCount = remindersList.get(lastRemId).DisplayScreens.size
        }

        fun getLastRemContent(position:Int):String{
            return remindersList[lastRemId].DisplayScreens[position].Description
        }

        //Is called when activity resumes. At that point activity does not know how it came into being
        fun WasTimerRunningBeforeClosing(context: Context, alarmTypeOrdinal: Int): Boolean {
            settings = context.getSharedPreferences("MinigamePref", Context.MODE_PRIVATE)
            val v = settings.getInt(context.getString(R.string.MinigameState), EnumStateType.ALL_INACTIVE.ordinal)
            if (v==EnumStateType.ALL_INACTIVE.ordinal)
                return false
            else {
                val index= settings.getInt(context.getString(R.string.AlarmTypeOrdinal), -1)
                return (index != -1 &&  (index==alarmTypeOrdinal))
            }
        }

        fun ResumeTimerBasedOnPref(context: Context, alarmType: EnumAlarmTypes): Int {
            val elapsedSecondsSinceSettingAlarm = (System.currentTimeMillis() - settings.getLong(context.getString(R.string.AlarmSetTimeMillies), System.currentTimeMillis())) / 1000
            this.currentState = settings.getInt(context.getString(R.string.MinigameState), EnumStateType.ALL_INACTIVE.ordinal)
            val remainingSecondsAtPause = settings.getInt(context.getString(R.string.TimerRemainingSec_Primary), primaryTimerDurString)

            initReminderTimes(context, true)

            // Check if there's a next reminder to consider
            if (!reminderTimes.isAllReminderDone()) {
                val nextReminderTimePoint = reminderTimes.reminderTimePoints[reminderTimes.nextReminderPointer]
                val timeSinceLastReminder = remainingSecondsAtPause - nextReminderTimePoint

                if (elapsedSecondsSinceSettingAlarm < timeSinceLastReminder) {
                    // No reminders have been missed
                    this.remainingSecondsPrimary = remainingSecondsAtPause - elapsedSecondsSinceSettingAlarm.toInt()
                } else {
                    // The next reminder has been missed
                    handleMissedReminders(context, elapsedSecondsSinceSettingAlarm - timeSinceLastReminder)
                    this.remainingSecondsPrimary = reminderTimes.reminderTimePoints[reminderTimes.nextReminderPointer]
                }
            }
            else {
                // All reminders have already occurred
                this.remainingSecondsPrimary = max(0, remainingSecondsAtPause - elapsedSecondsSinceSettingAlarm.toInt())
            }






            val gson = Gson()
            this.scores = gson.fromJson(settings.getString(context.getString(R.string.Scores), ""), Scores::class.java)
            //this.isTimerStopped = settings.getBoolean("Stopped", true)
            var retVal:Int=-1
            when(this.currentState){
                EnumStateType.PRMR_TMR_RNG_ONLY.ordinal->{
                    if(this.remainingSecondsPrimary<=0){
                        ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal) //CancelBothTimers()
                        retVal=R.id.minigameT1Score
                    }
                    else
                        retVal=R.id.fragment_minigame_t1_s1
                }
                EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal,EnumStateType.SEC_TMR_RNG_SHWNG_FDBCK.ordinal->{
                    var tempCurrentState=this.currentState
                    ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    when(tempCurrentState){
                        EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal ->
                            retVal = R.id.minigameT1RPre
                        EnumStateType.SEC_TMR_RNG_SHWNG_RMNDR.ordinal ->
                            retVal = R.id.minigameT1FPre
                        EnumStateType.SEC_TMR_RNG_SHWNG_FDBCK.ordinal ->{
                            if(reminderTimes.isAllReminderDone())
                                retVal = R.id.minigameT1Score
                            else{
                                ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)//since S5 does not resume the timer automaticaly it is needed.
                                retVal = R.id.minigameT1S5Fragment
                            }
                        }
                        else->{
                            Log.d("MyTag","SEC TMR Was RNG, should not come here")
                            ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
                            retVal = R.id.fragment_minigame_t1_s1
                        }
                    }//end when: checking SEC_TMR_RNG related cases individually
                }//end when: checking SEC_TMR_RNG related cases in a group
                EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_R_PRE.ordinal -> retVal=R.id.minigameT1RPre
                EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_F_PRE.ordinal -> retVal=R.id.minigameT1FPre
                EnumStateType.PRMR_TMR_PSD.ordinal -> retVal=R.id.fragment_minigame_t1_s1
                EnumStateType.ENTRY_WAIT.ordinal -> retVal=R.id.entryFragment
                EnumStateType.EXIT_WAIT.ordinal -> retVal=R.id.exitFragment
                EnumStateType.SHWNG_RMNDR_PAGER.ordinal -> retVal=R.id.pagerFragment
                EnumStateType.SEC_TMR_RNG_SHWNG_RMNDR.ordinal -> retVal=R.id.minigameT1R1
                EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_R_IDLE.ordinal->retVal=R.id.minigameT1RIdle
                EnumStateType.MENU_PAGER.ordinal->retVal=R.id.pagerMenu
                EnumStateType.ANSWER_MENU.ordinal->retVal=R.id.answerMenu
                EnumStateType.RECORDER.ordinal->retVal=R.id.recordingFragment
                EnumStateType.GENERATE.ordinal->retVal=R.id.generateMenu
                else->{
                    Log.d("MyTag","Either Was transitioning or was inactive before, should not come here")
                    ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
                    retVal = R.id.fragment_minigame_t1_s1
                }
            }

            //Invalidate preference value of alarmtype and state.
            with(settings.edit()){
                putInt(context.getString(R.string.MinigameState), EnumStateType.ALL_INACTIVE.ordinal)//to indicate that the activity is running foreground
                putInt(context.getString(R.string.AlarmTypeOrdinal),EnumAlarmTypes.UNINITIALIZED.ordinal)//to indicate
                apply()
            }
            //Resume the timer, current state and remaining times are already set from pref.
            if(currentState != EnumStateType.ALL_INACTIVE.ordinal)
                InitializeAndStartTimer(context, alarmType,null,null )//assume timerStartTime is already assigned
            return retVal
        }

        fun handleGameFinished(context: Context) {
            // The game has finished, so handle this case appropriately
            // For example, show a notification to the user that the game has finished
            showGameFinishedNotification(context)
            // Reset the timer or perform any other necessary cleanup
            // ...
        }

        fun showGameFinishedNotification(context: Context) {
            // Build and show the notification that the game has finished
            // ...
        }


        /*fun calculateMissedReminders(): Int {
            val currentTime = System.currentTimeMillis()
            val lastReminderTime = settings.getLong(context.getString(R.string.AlarmSetTimeMillies), currentTime)
            val elapsedSeconds = (currentTime - lastReminderTime) / 1000
            return reminderTimes.reminderTimePoints.count { it <= elapsedSeconds }
        }*/

        /*fun handleMissedReminders(context: Context, elapsedSeconds: Long) {
            // Determine how many reminders were missed
            val missedReminders = reminderTimes.reminderTimePoints.count { it <= elapsedSeconds }

            // Handle the missed reminders
            // For example, show a summary notification or update the UI accordingly
            // If there are missed reminders, show a notification
            if (missedReminders > 0) {
                showMissedRemindersNotification(context, missedReminders)
            }

            // Set the next reminder point
            //reminderTimes.nextReminderPointer = reminderTimes.reminderTimePoints.indexOfFirst { it > elapsedSeconds }.takeIf { it >= 0 } ?: 0
            //remainingSecondsPrimary = reminderTimes.reminderTimePoints.getOrNull(reminderTimes.nextReminderPointer)?.minus(elapsedSeconds.toInt()) ?: remainingSecondsPrimary
        }*/

        // Call this method whenever the user interacts with the app
        /*fun updateUserLastInteractionTime(context: Context) {
            val currentTime = System.currentTimeMillis()
            with(settings.edit()){
                putLong(context.getString(R.string.LastInteractionTime), currentTime)
                apply()
            }
        }*/

        // Call this method to check if we should show a notification
        /*fun shouldShowMissedReminderNotification(context: Context): Boolean {
            val lastInteractionTime = settings.getLong(context.getString(R.string.LastInteractionTime), 0)
            val currentTime = System.currentTimeMillis()
            val timeSinceLastInteraction = currentTime - lastInteractionTime

            // Check if 5 minutes have passed since the last interaction
            return timeSinceLastInteraction >= 5 * 60 * 1000
        }*/

        /*fun handleMissedReminders(context: Context, missedCount: Int) {
            if (shouldShowMissedReminderNotification(context)) {
                showMissedRemindersNotification(context, missedCount)
            }
        }*/

        fun handleMissedReminders(context: Context, diff: Long) {
            // Check if the elapsed time since setting the alarm is greater than 5 minutes
            if (diff >= 5 * 60) {
                // Show the notification for missed reminders
                showMissedRemindersNotification(context) // You can still pass 1 or calculate the actual missed count
            }
        }

        private fun showMissedRemindersNotification(context: Context) {
            val notificationIntent = Intent(context, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                .setContentTitle("Missed Reminders")
                .setContentText("You have missed next reminder. Tap to view.")
                .setSmallIcon(R.drawable.done) // Replace with your app's icon.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(MISSED_REMINDERS_NOTIFICATION_ID, notification)
        }


        /*fun ResumeTimerBasedOnPref(context: Context, alarmType: EnumAlarmTypes):Int{
            val elapsedSecondsSinceSettingAlarm = (System.currentTimeMillis() - settings.getLong(context.getString(R.string.AlarmSetTimeMillies),System.currentTimeMillis()))/1000
            this.currentState= settings.getInt(context.getString(R.string.MinigameState),EnumStateType.ALL_INACTIVE.ordinal)
            //Update remaining seconds of primary timer. Consider screen idle time only when primary timer was running last time.
            if(this.currentState==EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)
            //this.remainingSecondsPrimary=settings.getInt(context.getString(R.string.TimerRemainingSec_Primary), settings.getInt(context.getString(R.string.primaryTimerDurString), context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)))
                this.remainingSecondsPrimary=settings.getInt(context.getString(R.string.TimerRemainingSec_Primary), settings.getInt(context.getString(R.string.primaryTimerDurString), context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)))-elapsedSecondsSinceSettingAlarm.toInt()
            else
                this.remainingSecondsPrimary=settings.getInt(context.getString(R.string.TimerRemainingSec_Primary), settings.getInt(context.getString(R.string.primaryTimerDurString), context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)))

            //Actually remaining seconds of scndary timer is not being utilized.
            //this.remainingSecondsSecondary=settings.getInt(context.getString(R.string.TimerRemainingSec_Secondary), settings.getInt(context.getString(R.string.primaryTimerDurString), context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)))-elapsedSecondsSinceSettingAlarm.toInt()
            initReminderTimes(context, true)
            val gson = Gson()
            this.scores = gson.fromJson(settings.getString(context.getString(R.string.Scores), ""), Scores::class.java)
            //this.isTimerStopped = settings.getBoolean("Stopped", true)
            var retVal:Int=-1
            when(this.currentState){
                EnumStateType.PRMR_TMR_RNG_ONLY.ordinal->{
                    if(this.remainingSecondsPrimary<=0){
                        ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal) //CancelBothTimers()
                        retVal=R.id.minigameT1Score
                    }
                    else
                        retVal=R.id.fragment_minigame_t1_s1
                }
                EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal,EnumStateType.SEC_TMR_RNG_SHWNG_FDBCK.ordinal->{
                    var tempCurrentState=this.currentState
                    ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                    when(tempCurrentState){
                        EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal ->
                            retVal = R.id.minigameT1RPre
                        EnumStateType.SEC_TMR_RNG_SHWNG_RMNDR.ordinal ->
                            retVal = R.id.minigameT1FPre
                        EnumStateType.SEC_TMR_RNG_SHWNG_FDBCK.ordinal ->{
                            if(reminderTimes.isAllReminderDone())
                                retVal = R.id.minigameT1Score
                            else{
                                ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)//since S5 does not resume the timer automaticaly it is needed.
                                retVal = R.id.minigameT1S5Fragment
                            }
                        }
                        else->{
                            Log.d("MyTag","SEC TMR Was RNG, should not come here")
                            ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
                            retVal = R.id.fragment_minigame_t1_s1
                        }
                    }//end when: checking SEC_TMR_RNG related cases individually
                }//end when: checking SEC_TMR_RNG related cases in a group
                EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_R_PRE.ordinal -> retVal=R.id.minigameT1RPre
                EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_F_PRE.ordinal -> retVal=R.id.minigameT1FPre
                EnumStateType.PRMR_TMR_PSD.ordinal -> retVal=R.id.fragment_minigame_t1_s1
                EnumStateType.ENTRY_WAIT.ordinal -> retVal=R.id.entryFragment
                EnumStateType.EXIT_WAIT.ordinal -> retVal=R.id.exitFragment
                EnumStateType.SHWNG_RMNDR_PAGER.ordinal -> retVal=R.id.pagerFragment
                EnumStateType.SEC_TMR_RNG_SHWNG_RMNDR.ordinal -> retVal=R.id.minigameT1R1
                EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_R_IDLE.ordinal->retVal=R.id.minigameT1RIdle
                EnumStateType.MENU_PAGER.ordinal->retVal=R.id.pagerMenu
                EnumStateType.ANSWER_MENU.ordinal->retVal=R.id.answerMenu
                EnumStateType.RECORDER.ordinal->retVal=R.id.recordingFragment
                EnumStateType.GENERATE.ordinal->retVal=R.id.generateMenu
                else->{
                    Log.d("MyTag","Either Was transitioning or was inactive before, should not come here")
                    ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
                    retVal = R.id.fragment_minigame_t1_s1
                }
            }

            //Invalidate preference value of alarmtype and state.
            with(settings.edit()){
                putInt(context.getString(R.string.MinigameState), EnumStateType.ALL_INACTIVE.ordinal)//to indicate that the activity is running foreground
                putInt(context.getString(R.string.AlarmTypeOrdinal),EnumAlarmTypes.UNINITIALIZED.ordinal)//to indicate
                apply()
            }
            //Resume the timer, current state and remaining times are already set from pref.
            if(currentState != EnumStateType.ALL_INACTIVE.ordinal)
                InitializeAndStartTimer(context, alarmType,null,null )//assume timerStartTime is already assigned
            return retVal
        }*/

        fun ResumeTimerBasedOnPrefAfterPause(context: Context, alarmType: EnumAlarmTypes){
            val elapsedSecondsSinceSettingAlarm = (System.currentTimeMillis() - settings.getLong(context.getString(R.string.AlarmSetTimeMillies),System.currentTimeMillis()))/1000
            this.currentState= settings.getInt(context.getString(R.string.MinigameState),EnumStateType.ALL_INACTIVE.ordinal)
            //Update remaining seconds of primary timer. Consider screen idle time only when primary timer was running last time.
            if(this.currentState==EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)
            //this.remainingSecondsPrimary=settings.getInt(context.getString(R.string.TimerRemainingSec_Primary), settings.getInt(context.getString(R.string.primaryTimerDurString), context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)))
                this.remainingSecondsPrimary=settings.getInt(context.getString(R.string.TimerRemainingSec_Primary), settings.getInt(context.getString(R.string.primaryTimerDurString), context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)))-elapsedSecondsSinceSettingAlarm.toInt()
            else
                this.remainingSecondsPrimary=settings.getInt(context.getString(R.string.TimerRemainingSec_Primary), settings.getInt(context.getString(R.string.primaryTimerDurString), context.resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)))

            with(settings.edit()){
                putInt(context.getString(R.string.TimerRemainingSec_Primary), remainingSecondsPrimary)
                putLong(context.getString(R.string.AlarmSetTimeMillies),System.currentTimeMillis())
                apply()
                //putBoolean(context.getString(R.string.IsPrimaryTimerActive), true)
            }
        }

        fun initScoreAndReminderTimes(context: Context){
            scores = Scores()//initialize with all 0s

            var r: String? = MyTimer.settings.getString(
                context.getString(R.string.ReminderTimes),
                ""
            )
            if (r!!.length  > 0)
                initReminderTimes(context, true)
            else
                initReminderTimes(context, false)
        }

        fun resetScoreAndReminder() {
            if(this::scores.isInitialized) {
                this.scores.reset()
            }
            if(this::reminderTimes.isInitialized) {
                MyTimer.resetReminderTimes()
            }
        }

        fun resetScore() {
            if(this::scores.isInitialized) {
                this.scores.reset()
            }
            else{
                scores= Scores()
            }
        }

        fun updateScore(isCorrect: Boolean, isIncorrect: Boolean){
            if(isCorrect)
                this.scores.scoreCorrect++
            else if (isIncorrect)
                this.scores.scoreIncorrect++
        }

        fun getTotalScore():Int{
            return this.scores.scoreCorrect
        }

        fun InitializeAndStartTimer(context: Context, alarmType: EnumAlarmTypes, currentState:Int?, remainingSecondsPrimary: Int?){
            this.context=context
            if (remainingSecondsPrimary != null) {
                this.remainingSecondsPrimary=remainingSecondsPrimary
            }
            //this.totalTimerDurationSecondPrimary=context.resources.getInteger(R.integer.timerDurationSecondMinigameT1)
            if (currentState != null) {
                this.currentState=currentState
            }//timerState=1//isTimerActive =true             isTimerPaused=false
            MinigameTypeOrdinal = alarmType.ordinal//set alarm type
            val mainLooperHandler = InitializeTimer(context)//the timer object is initialized
            mainLooperHandler.postDelayed(this.timerPrimary, 1000)
        }

        fun updateTimerTextIfVisible(context: Context) {

            val activity = context as? MinigameT1 ?: return // Safely cast and return if not the expected activity
            val currentNavHost = activity.supportFragmentManager.primaryNavigationFragment as? NavHostFragment ?: return // Safely cast and return if not found
            // Proceed only if the NavHostFragment is added
            if (currentNavHost.isAdded) {
                if (currentNavHost.navController.currentDestination?.label == "Minigame T1 S1") // Timer is shown only in this fragment
                {
                    val fragT1S1: MinigameT1S1Fragment? =
                        currentNavHost.childFragmentManager.fragments.last() as? MinigameT1S1Fragment
                    if (fragT1S1 != null && fragT1S1.isVisible()) {
                        fragT1S1.changeTimerText(ConvertInTimeStr(remainingSecondsPrimary))
                        fragT1S1.setProgress(remainingSecondsPrimary)
                    }
                }
                else if (currentNavHost.navController.currentDestination?.label == "fragment_minigame_t1_s5") {
                    val fragT1S5: MinigameT1S5Fragment? =
                        currentNavHost.childFragmentManager.fragments.last() as? MinigameT1S5Fragment
                    if (fragT1S5 != null && fragT1S5.isVisible()) {
                        if (!reminderTimes.isAllReminderDone() && reminderTimes.nextReminderPointer < reminderTimes.reminderTimePoints.size) {
                            val remainingSecsPrimaryMinusNextReminder = remainingSecondsPrimary - reminderTimes.reminderTimePoints[reminderTimes.nextReminderPointer]
                            fragT1S5.changeTimerText(ConvertInTimeStr(remainingSecsPrimaryMinusNextReminder))
                        }
                        else{
                            fragT1S5.changeState(ConvertInTimeStr(remainingSecondsPrimary))
                        }
                    }
                }
            }

        }

        public fun resetAndupdate(context: Context){
            val currentNavHost = (context as MinigameT1).supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null && currentNavHost.isAdded) {
                currentNavHost.navController.navigate(R.id.action_global_minigameT1S1)
                if (currentNavHost.navController.currentDestination?.label=="Minigame T1 S1")//Timer is shown only in this fragment
                {
                    val fragT1S1:MinigameT1S1Fragment? = currentNavHost.childFragmentManager.fragments.last() as? MinigameT1S1Fragment
                    if (fragT1S1 != null && fragT1S1.isVisible()){
                        if(remainingSecondsPrimary!=-1) {
                            fragT1S1.changeTimerText(ConvertInTimeStr(remainingSecondsPrimary))
                            fragT1S1.setProgress(remainingSecondsPrimary)
                        }else{
                            fragT1S1.changeTimerText(ConvertInTimeStr(primaryTimerDurString))
                            fragT1S1.setProgress(primaryTimerDurString )
                        }
                    }
                }
            }
        }

        public fun reset(context: Context){
            val currentNavHost = (context as MinigameT1).supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null) {
                currentNavHost.navController.navigate(R.id.action_global_minigameT1S1)
            }
        }

        public fun showRemEntry(context: Context){
            val currentNavHost = (context as MinigameT1).supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null) {
                currentNavHost.navController.navigate(R.id.minigameT1RIdle)
            }
        }

        public fun continueRemExit(context: Context){
            ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)//since S5 does not resume the timer automaticaly it is needed.
            (context as MinigameT1).supportFragmentManager.fragments.last().findNavController().navigate(R.id.minigameT1S5Fragment)
        }

        private fun checkIfReminderPossible(context: Context){
            if(this.reminderTimes.nextReminderPointer>=0 &&
                this.reminderTimes.nextReminderPointer < this.reminderTimes.reminderTimePoints.size &&
                remainingSecondsPrimary <= this.reminderTimes.reminderTimePoints[this.reminderTimes.nextReminderPointer]){
                //do a reminder
                Log.d("MyTag", "Reminder ${this.reminderTimes.nextReminderPointer + 1} occurred for time point ${this.reminderTimes.reminderTimePoints[this.reminderTimes.nextReminderPointer]}")
                //pauseBothTimers() this is done on
                vibrate(context)
                (context as MinigameT1).supportFragmentManager.fragments.last().findNavController().navigate(R.id.action_global_minigameT1RPre)
            }
        }

        fun causeReminder(){
            this.reminderTimes.nextReminderPointer++
            totalRemindersRecieved ++
        }

        fun vibrate(context: Context) {

            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(context.resources.getInteger(R.integer.vibrationDurationMilliSec).toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
            } else vibrator.vibrate(context.resources.getInteger(R.integer.vibrationDurationMilliSec).toLong())
        }

        private fun cleanupAfterTimerFinishedAndGoToScore(context: Context){
            ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
            val currentNavHost = (context as MinigameT1).supportFragmentManager.primaryNavigationFragment as NavHostFragment
            if(currentNavHost != null)
                currentNavHost.navController.navigate(R.id.minigameT1Score)
        }

        fun InitializeTimer(context: Context):Handler {
            val mainLooperHandler = Handler(Looper.getMainLooper())
            //val handler = Handler()
            lastSeenSecond=Calendar.getInstance().get(Calendar.SECOND)

            Log.d("MyTag", "Timer initializing")
            this.timerPrimary = object : Runnable {
                override fun run() {
                    if(!isTimerStopped) {
                        if (currentState != EnumStateType.ALL_INACTIVE.ordinal) {//(timerState>0) {//isTimerActive
                            if (remainingSecondsPrimary <= 0) {
                                Log.d("MyTag", "Timer done running")
                                cleanupAfterTimerFinishedAndGoToScore(context)
                            }
                            else if (currentState == EnumStateType.PRMR_TMR_RNG_ONLY.ordinal) {//(timerState==1){//!isTimerPaused
                                //Log.d("MyTag", "$remainingSeconds seconds remaining")
                                if (Calendar.getInstance().get(Calendar.SECOND) != lastSeenSecond) {
                                    remainingSecondsPrimary--
                                    Log.d(
                                        "MyTag",
                                        "remaining seconds subtracted and it is: $remainingSecondsPrimary"
                                    )
                                    updateTimerTextIfVisible(context)
                                    checkIfReminderPossible(context)
                                } else {
                                    Log.d("MyTag", "One second has not passed. Continuing")
                                }
                            }
                            else {
                                Log.d("MyTag", "Just ran one sec without doing anything.")
                            }
                            //regardless of what state, as long as timer is active, run again in 1 second and remember this second. Checking is necessary as it might be changed by previous code.
                            if (currentState != EnumStateType.ALL_INACTIVE.ordinal
                            ) {
                                mainLooperHandler.postDelayed(this, 1000)
                                lastSeenSecond = Calendar.getInstance().get(Calendar.SECOND)
                            }
                        }//end if to check if timer is active
                    }
                }//end fun run
            }
            return mainLooperHandler
        }

        fun initReminderTimes(context: Context, fetchFromPref: Boolean) {
            if (fetchFromPref){
                val gson = Gson()
                this.reminderTimes=gson.fromJson(settings.getString(context.getString(R.string.ReminderTimes), ""), ReminderTimes::class.java)
            }
            else{
                this.reminderTimes= ReminderTimes(context,MyTimer.primaryTimerDurString)
            }
        }

        fun ChangeCurrentState(currentStateOrdinal:Int){
            this.currentState=currentStateOrdinal
        }

        //is called when the minigame activity is paused.
        fun StopTimerAndSetAlarm(alarmType: EnumAlarmTypes, context: Context) {
            var tempCurrentState = currentState
            ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
            val reminderTimesString = Gson().toJson(this.reminderTimes)
            val scoreString = Gson().toJson(this.scores)
            //val avatarProfileString = Gson().toJson(avatarProfile)

            with(settings.edit()) {
                putInt(context.getString(R.string.AlarmTypeOrdinal), alarmType.ordinal)
                putInt(context.getString(R.string.MinigameState), tempCurrentState) //very important
                putInt(context.getString(R.string.TimerRemainingSec_Primary), remainingSecondsPrimary)

                putInt(context.getString(R.string.lastShowenReminder), lastRemId)

                putInt(context.getString(R.string.totalRun), totalRun)
                putInt(context.getString(R.string.totalCompleted), totalCompleted)
                putInt(context.getString(R.string.totalStopped), totalStopped)
                putInt(context.getString(R.string.totalTime), totalTime)
                putInt(context.getString(R.string.totalAlgos), totalAlgos)
                putInt(context.getString(R.string.GtotalRun), GtotalRun)
                putInt(context.getString(R.string.GtotalCompleted), GtotalCompleted)
                putInt(context.getString(R.string.GtotalStopped), GtotalStopped)
                putInt(context.getString(R.string.GtotalTime), GtotalTime)

                putInt(context.getString(R.string.totalRemindersString), totalRemindersString)

                putString(context.getString(R.string.selectedTypeRec), selectedType)

                putInt(context.getString(R.string.totalRemindersRecieved), totalRemindersRecieved)

                putString(context.getString(R.string.ReminderTimes), reminderTimesString) //save reminder times
                putString(context.getString(R.string.Scores), scoreString)
                putLong(context.getString(R.string.AlarmSetTimeMillies), System.currentTimeMillis())
                //putString(context.getString(R.string.AvatarProfile), avatarProfileString)
                apply()
                //commit()
            }

            //Set alarm timer in alarm manager.
            var alarmFromNowInSec = getAlarmFromNowInSec(tempCurrentState, context)
            //setAlarm(alarmFromNowInSec, context)
            // Show a notification instead of setting an alarm

            // Schedule the alarm to trigger the BroadcastReceiver
            setAlarm(alarmFromNowInSec, context)
            Log.d("MyTag", "Alarm set to show notification after $alarmFromNowInSec seconds")

            //Log.d("MyTag", "Notification scheduled to show after $alarmFromNowInSec seconds")
            //Log.d("MyTag", "Alarm set after $alarmFromNowInSec seconds")
        }

        /*fun SaveValues(context: Context) {
            //val reminderTimesString = Gson().toJson(this.reminderTimes)
            //val scoreString = Gson().toJson(this.scores)
            val avatarProfileString = Gson().toJson(avatarProfile)

            with(settings.edit()) {
                putString(context.getString(R.string.AvatarProfile), avatarProfileString)
                //putInt(context.getString(R.string.MinigameState), currentState)//very important
                //putInt(context.getString(R.string.TimerRemainingSec_Primary), remainingSecondsPrimary)

                // (other integers follows as above...)

                //putString(context.getString(R.string.ReminderTimes),reminderTimesString)//save reminder times
                //putString(context.getString(R.string.Scores),scoreString)
                apply()
            }
        }*/

        fun setAlarm(secondsFromNow: Int, context: Context) {
            val alarmIntent = Intent(context, MinigameT1.ReminderBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val triggerTime = System.currentTimeMillis() + secondsFromNow * 1000
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }

        /*private fun setAlarm(secondsFromNow:Int,context: Context){
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.SECOND, secondsFromNow)

            val intent  =  Intent(context, MinigameT1::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }*/

        private  fun getAlarmFromNowInSec(tempCurrentState:Int,context: Context):Int{
            var alarmFromNowInSec:Int
            when(tempCurrentState){
                EnumStateType.PRMR_TMR_RNG_ONLY.ordinal->{
                    if(this.reminderTimes.nextReminderPointer>=0 && this.reminderTimes.nextReminderPointer<this.reminderTimes.reminderTimePoints.size)
                        alarmFromNowInSec =remainingSecondsPrimary + 1 - this.reminderTimes.reminderTimePoints[this.reminderTimes.nextReminderPointer]
                    else
                        alarmFromNowInSec = remainingSecondsPrimary
                }
                EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_R_PRE.ordinal, EnumStateType.ALL_TMR_PSD_WAITING_USR_INPUT_F_PRE.ordinal->
                    alarmFromNowInSec=   context.resources.getInteger(R.integer.delaySecToWakeWhenWaitingForUser)

                EnumStateType.SEC_TMR_RNG_SNOOZE_TO_ANS_QUES.ordinal,EnumStateType.SEC_TMR_RNG_SHWNG_RMNDR.ordinal,EnumStateType.SEC_TMR_RNG_SHWNG_FDBCK.ordinal->
                    alarmFromNowInSec= 360//context.resources.getInteger(R.integer.delaySecDefaultToWakeWhenSecondaryTimerRunning)
                else->{//includes: ALL_INACTIVE, ALL_TMR_PSD_TRANSITIONING
                    Log.d("MyTag","Unhandled state")
                    alarmFromNowInSec= 360//context.resources.getInteger(R.integer.delaySecDefaultToWakeWhenSecondaryTimerRunning)
                }
            }
            return alarmFromNowInSec
        }

        fun ConvertInTimeStr(secondPassed: Int):String{

            val S: Int = secondPassed % 60
            var H: Int = secondPassed / 60
            val M = H % 60
            H = H / 60


            //val minute= Math.floorDiv(secondPassed, 60)
            //return minute.toString().padStart(2, '0')+":"+(secondPassed%60).toString().padStart(2, '0')
            //return H.toString().padStart(2, '0')+":"+M.toString().padStart(2, '0')+":"+S.toString().padStart(2, '0')
            return H.toString()+"H "+M.toString()+"Min "+S.toString()+"Sec"
        }

        fun getReminderStat(isCompletedReminderElseRemainReminder:Boolean):Int{
            if(isCompletedReminderElseRemainReminder){
                if(this.reminderTimes.nextReminderPointer<=0)
                    return 0
                else
                    return this.reminderTimes.nextReminderPointer
            }
            else
                if(this.reminderTimes.nextReminderPointer<=0)
                    return this.reminderTimes.reminderTimePoints.size
                else
                    return this.reminderTimes.reminderTimePoints.size-this.reminderTimes.nextReminderPointer
        }

        fun resetReminderTimes(){
            this.reminderTimes.resetReminderTimes()
        }


        /*suspend fun isInternetConnected(context: Context): Boolean = withContext(Dispatchers.IO) {
            val hasConnection = hasNetworkConnection(context)
            return@withContext if (hasConnection) canAccessInternet(context) else false
        }*/

        private suspend fun hasNetworkConnection(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            lateinit var callback: ConnectivityManager.NetworkCallback
            var isCancelled = false

            val connectionResult = try {
                withTimeout(5000L) {
                    suspendCancellableCoroutine<Boolean> { cont ->
                        val request = NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                            .build()

                        callback = object : ConnectivityManager.NetworkCallback() {
                            override fun onAvailable(network: Network) {
                                super.onAvailable(network)
                                cont.resume(true) { connectivityManager.unregisterNetworkCallback(callback) }
                            }

                            override fun onLost(network: Network) {
                                super.onLost(network)
                            }
                        }

                        cont.invokeOnCancellation {
                            isCancelled = true
                            try {
                                connectivityManager.unregisterNetworkCallback(callback)
                            } catch (e: IllegalArgumentException) {}
                        }

                        connectivityManager.requestNetwork(request, callback)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                if (!isCancelled) {
                    try {
                        connectivityManager.unregisterNetworkCallback(callback)
                    } catch (e1: IllegalArgumentException) {}
                }
                false
            }

            return connectionResult
        }

        fun updateAvatarProfileListen(context: Context, newProfile: AvatarProfile) {
            avatarProfile = newProfile
            // Notify all listeners of the update
            listeners.forEach { it.onAvatarProfileUpdated(newProfile) }
        }

        fun addAvatarProfileUpdateListener(listener: AvatarProfileUpdateListener) {
            listeners.add(listener)
        }

        fun removeAvatarProfileUpdateListener(listener: AvatarProfileUpdateListener) {
            listeners.remove(listener)
        }

        interface AvatarProfileUpdateListener {
            fun onAvatarProfileUpdated(newProfile: AvatarProfile)
        }

        fun showTimerNotification(context: Context, secondsFromNow: Int, action: String, title:String, content:String) {
            val notificationIntent = Intent(context, MinigameT1::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("action", action) // Include the action string
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build the notification
            val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.arrow) // Replace with your timer icon.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            // Schedule the notification using AlarmManager
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val triggerTime = System.currentTimeMillis() + secondsFromNow * 1000
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }

        /*private suspend fun canAccessInternet(context: Context): Boolean {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            if (bluetoothAdapter?.isEnabled == true) {
                // Check if a peer Bluetooth device is connected to this device.
                val pairedDevices = if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                    bluetoothAdapter.bondedDevices
                if (pairedDevices.isNotEmpty()) {
                    // As a definitive solution to cover phone-shared internet access is unavailable, a simple working heuristic -
                    // requires internet access on both TRANSPORT_WIFI and/or TRANSPORT_CELLULAR when a Bluetooth device is connected
                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                }
            }

            return try {
                withTimeoutOrNull(2000L) {
                    val url = URL("https://www.google.com/generate_204")
                    val urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.useCaches = false
                    urlConnection.connectTimeout = 2000

                    urlConnection.connect()
                    (urlConnection.responseCode == 204 && urlConnection.contentLength == 0) // Perform HTTP response code check
                } ?: false
            } catch (e: SocketTimeoutException) {
                false
            } catch (e: Exception) {
                false
            }
        }*/

    }
}
