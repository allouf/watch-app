package com.incentives.prototype.minigameT1


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.FULL_WAKE_LOCK
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.*
import android.support.wearable.view.DismissOverlayView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.google.gson.Gson
import kotlin.random.Random

//import java.util.*

class MinigameT1 : FragmentActivity() {

    lateinit var repository: Repository

    var typeAI = 0
    var questionsCount = 1
    var recordedString = ""

    private lateinit var wakeLock: PowerManager.WakeLock
    lateinit var main: FrameLayout
    private lateinit var mDismissOverlayView : DismissOverlayView
    lateinit var swipeDetector: OnSwipeTouchListener

    var isSwipeAnimRendered:Boolean=false

    var startCodeRequested: Boolean = false
    fun startCodeRequested() {
        startCodeRequested = true
    }

    private var isDialogShown = false

    private fun showDialog(onDismissed: () -> Unit) {
        Log.d("MinigameT1", "Showing dialog")
        val animationDialog = AnimationDialog().apply {
            onDialogDismissed = {
                Log.d("MinigameT1", "Dialog dismissed, invoking callback")
                onDismissed() // Invoke the callback
                isDialogShown = false // Reset the flag
            }
        }
        animationDialog.show(supportFragmentManager, "AnimationDialog")
    }

    private fun navigateToFragment(fragmentId: Int) {
        Log.d("MinigameT1", "Navigating to fragment: $fragmentId")
        val currentNavHost = supportFragmentManager.primaryNavigationFragment as? NavHostFragment
        currentNavHost?.navController?.navigate(fragmentId)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame_t1)

        repository = Repository.getInstance(application as MyApplication)

        repository.actionData.observe(this, Observer { actionData ->
            when (actionData.action) {
                "StartAndPlay" -> handleStartAndPlay()
                "PauseAndStop" -> handlePauseAndStop()
                "SetTimer" -> handleSetTimer(actionData.parameters)
                "SetAlgorithm" -> handleSetAlgorithm(actionData.parameters)
                "SetAvatar" -> handleSetAvatar(actionData.parameters)
            }
        })

        handleIntent(intent)

        main = findViewById(R.id.main)
        mDismissOverlayView = findViewById(R.id.dismiss) as DismissOverlayView
        swipeDetector = object : OnSwipeTouchListener(this@MinigameT1) {
            override fun onSwipeRight() {
                //super.onSwipeRight()

                val navHostFragment: Fragment? =
                    supportFragmentManager.findFragmentById(R.id.nav_host_minigame_t1_fragment)
                var curr: Fragment? = navHostFragment?.childFragmentManager?.fragments?.get(0)
                if (curr is MinigameT1FPre) { // do something with f
                    MyTimer.isSwipedBack = true
                    val currentNavHost = this@MinigameT1.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                    if(currentNavHost != null) {
                        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                        val bundle = Bundle()
                        bundle.putBoolean("causeReminder", false)
                        currentNavHost.navController.navigate(R.id.minigameT1R1,bundle)
                    }
                    //Toast.makeText(this@MinigameT1, "Right Swipe", Toast.LENGTH_SHORT).show()
                }
                else if (curr is MinigameT1R1) { // do something with f
                    MyTimer.isSwipedBack = true
                    val currentNavHost = this@MinigameT1.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                    if(currentNavHost != null) {
                        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                        currentNavHost.navController.navigate(R.id.minigameT1RIdle)
                    }
                    //Toast.makeText(this@MinigameT1, "Right Swipe", Toast.LENGTH_SHORT).show()
                }
                else if (curr is MinigameT1S2Fragment ||
                         curr is MinigameT1S4Fragment ||
                         curr is MinigameT1S5Fragment) {
                    //this@MinigameT1.finish()

                    this@MinigameT1.finish()
                    this@MinigameT1.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                }
                else if (curr is PagerFragment) { // do something with f
                    curr.pager.setCurrentItem(curr.pager.currentItem -1,true)
                }
                else
                    Toast.makeText(this@MinigameT1, "Right Swipe", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeLeft() {
                val navHostFragment: Fragment? =
                    supportFragmentManager.findFragmentById(R.id.nav_host_minigame_t1_fragment)
                var curr: Fragment? = navHostFragment?.childFragmentManager?.fragments?.get(0)
                if (curr is MinigameT1R1) { // do something with f
                    MyTimer.isSwipedBack = false
                    val currentNavHost = this@MinigameT1.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                    if(currentNavHost != null) {
                        MyTimer.getLastRemPages()
                        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                        if(MyTimer.lastRemPagesCount > 0)
                            currentNavHost.navController.navigate(R.id.action_minigameT1R1_to_pagerFragment)
                        else
                            currentNavHost.navController.navigate(R.id.minigameT1FPre)
                    }
                }
                else if (curr is MenuFragment) { // do something with f
                    val currentNavHost = this@MinigameT1.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                    if(currentNavHost != null) {
                        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                        currentNavHost.navController.navigate(R.id.pagerFragment)
                    }
                }
                else if (curr is GenerateFragment || curr is AnswerFragment) { // do something with f
                    val currentNavHost = this@MinigameT1.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                    if(currentNavHost != null) {
                        MyTimer.ChangeCurrentState(EnumStateType.ALL_TMR_PSD_TRANSITIONING.ordinal)
                        currentNavHost.navController.navigate(R.id.pagerMenu)
                    }
                }
                else if (curr is PagerFragment) { // do something with f
                    curr.pager.setCurrentItem(curr.pager.currentItem +1,true)
                }
                else
                    Toast.makeText(this@MinigameT1, "Left Swipe", Toast.LENGTH_SHORT).show()
            }

            override fun onLongPress() {
                mDismissOverlayView.show()
                Toast.makeText(this@MinigameT1, "Long Press", Toast.LENGTH_LONG).show()
            }
        }
        main.setOnTouchListener(swipeDetector)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.getStringExtra("action")?.let { action ->
            when (action) {
                "ACTION_UPDATE_TIMER" ->
                    MyTimer.Companion.updateTimerTextIfVisible(this@MinigameT1)
                "RESET_UPDATE_TIMER" ->
                    MyTimer.Companion.resetAndupdate(this@MinigameT1)
                "ACTION_SHOW_REMINDER" ->
                    MyTimer.Companion.resetAndupdate(this@MinigameT1)
                "CONTINUE_EXIT_REMINDER" ->
                    MyTimer.Companion.continueRemExit(this@MinigameT1)
                "TIMER_REMINDER_ACTION" -> // Handle the new action for the timer reminder
                    handleTimerReminder()
            }
        }
    }

    private fun handleTimerReminder() {
        // Handle the timer reminder action when the notification is tapped
    }

    private fun handlePauseAndStop() {
        MyTimer.ResumeTimerBasedOnPrefAfterPause(this, EnumAlarmTypes.valueOf("MinigameT1"))
        if (!MyTimer.isTimerPaused && !MyTimer.remainingSecondsPrimary.equals(-1) && !MyTimer.isTimerStopped) {
            MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_PSD.ordinal)
            MyTimer.isTimerPaused = true
            MyTimer.isTimerStopped = false
            MyTimer.vibrate(this)
            with(MyTimer.settings.edit()) {
                putInt(
                    getString(R.string.MinigameState),
                    EnumStateType.PRMR_TMR_PSD.ordinal
                )
                apply()
            }
        }
    }


    private fun handleStartAndPlay() {
        // Start the timer or perform any action needed
        if (!MyTimer.isTimerPaused && MyTimer.isTimerStopped)
        {
            MyTimer.totalRun += 1
            MyTimer.GtotalRun += 1
            Log.d("MyTag", "Timer starting from beginning")
            MyTimer.InitializeAndStartTimer(
                this,
                EnumAlarmTypes.valueOf("MinigameT1"),
                EnumStateType.PRMR_TMR_RNG_ONLY.ordinal,
                MyTimer.settings.getInt(
                    getString(R.string.primaryTimerDurString),
                    resources.getInteger(R.integer.primaryTimerDurSecMinigameT1)
                )
            )
        }
        else
        {
            MyTimer.ChangeCurrentState(EnumStateType.PRMR_TMR_RNG_ONLY.ordinal)
            with(this.getSharedPreferences("MinigamePref", Context.MODE_PRIVATE).edit()) {
                putInt(
                    getString(R.string.MinigameState),
                    EnumStateType.PRMR_TMR_RNG_ONLY.ordinal
                )
                apply()
            }
        }
        MyTimer.isTimerPaused = false
        MyTimer.isTimerStopped = false
        MyTimer.vibrate(this)
    }

    fun String.matchLabels(): String? {
        return MyTimer.labels.firstOrNull { this.contains("#$it") }
    }

    private fun handleSetAlgorithm(parameters: Map<String, Any?>){
        if (!isDialogShown) {
            showDialog { navigateToFragment(R.id.fragment_minigame_t1_s3) }
            isDialogShown = true
        }
        val splittedMsg = parameters["splittedMsg"] as? List<String>
        MyTimer.vibrate(this)
        if (splittedMsg != null) {
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
                val algo = gson.fromJson(splittedMsg[1], AlgorithmFile::class.java)

                val label = algo.Description.matchLabels()
                if (label != null) {
                    MyTimer.currLable = label
                    MyTimer.algoDescription = algo.Description
                    MyTimer.algoTitle = algo.Name
                    MyTimer.CurrentProgress = algo.CurrentProgress
                    MyTimer.CurrentAnalytics = algo.CurrentAnalytics

                    val algosIds = AlgosIds.getInstance(this)

                    // Check if the received algo ID already exists in the list
                    if (algosIds.containsId(algo.Id)) {
                        // The ID exists, handle the logic here as needed
                        MyTimer.flatAlgoData(algo,this)
                    }
                    else {
                        // The ID does not exist, add it to the list
                        algosIds.addId(algo.Id)
                        MyTimer.flatAlgoData(algo,this)
                        MyTimer.totalRun = 0
                        MyTimer.totalCompleted = 0
                        MyTimer.totalStopped = 0
                        MyTimer.totalRemindersRecieved = 0
                        MyTimer.totalAlgos += 1
                        MyTimer.totalTime = 0
                        algosIds.saveIds()
                    }

                }
            }

        }
    }


    private fun handleSetAvatar(parameters: Map<String, Any?>){
        showDialog { navigateToFragment(R.id.fragment_minigame_t1_s1) }
        val splittedMsg = parameters["splittedMsg"] as? List<String>
        MyTimer.vibrate(this)
        if (splittedMsg != null) {
            if (splittedMsg.size > 1) {
                try {
                    val avatarProfile = Gson().fromJson(splittedMsg[1], AvatarProfile::class.java)
                    val randomValue = Random.nextInt(25, 125)
                    for ((i, progressBarUnity) in avatarProfile.ProgressBars.withIndex()) {
                        progressBarUnity.ProgressValue = randomValue.toFloat()
                    }
                    MyTimer.avatarProfile = avatarProfile

                    MyTimer.updateAvatarProfile(this,avatarProfile)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun handleSetTimer(parameters: Map<String, Any?>){

        val splittedMsg = parameters["splittedMsg"] as? List<String>
        if (splittedMsg != null) {
            if (splittedMsg.size > 1) {
                val timer: Int = splittedMsg[1].toInt()
                val vibration: Int = splittedMsg[2].toInt()
                MyTimer.isTimerPaused = false
                MyTimer.isTimerStopped = true
                MyTimer.ChangeCurrentState(EnumStateType.ALL_INACTIVE.ordinal)
                MyTimer.remainingSecondsPrimary = timer
                MyTimer.primaryTimerDurString = timer
                MyTimer.totalRemindersString = vibration

                with(MyTimer.settings.edit()) {
                    putInt(
                        applicationContext.getString(R.string.primaryTimerDurString),
                        MyTimer.primaryTimerDurString
                    )
                    putInt(
                        applicationContext.getString(R.string.TimerRemainingSec_Primary),
                        MyTimer.remainingSecondsPrimary
                    )
                    putInt(
                        applicationContext.getString(R.string.MinigameState),
                        EnumStateType.ALL_INACTIVE.ordinal
                    )
                    apply()
                }

                MyTimer.initReminderTimes(this, false)
                val reminderTimesString = Gson().toJson(MyTimer.reminderTimes)

                with(MyTimer.settings.edit()) {
                    putString(
                        applicationContext.getString(R.string.ReminderTimes),
                        reminderTimesString
                    )
                    commit()
                }
                navigateToFragment(R.id.fragment_minigame_t1_s1)
                MyTimer.Companion.updateTimerTextIfVisible(this)
                MyTimer.vibrate(this)
            }
        }
    }

    /*private fun bindMessageService() {
        Intent(this, MessageService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }*/

    /*private fun unbindMessageService() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }*/

    // Call this method when you want to show the notification
    /*private fun bringAppToFront() {
        val intent = Intent(this, MessageService::class.java).apply {
            action = MessageService.ACTION_SHOW_NOTIFICATION
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }*/

    public override fun onResume() {
        super.onResume()
        Log.d("MyTag", "Minigame T1 On Resume method")
        //Check from the registry if timer was running. Set the variable accordingly.
        if (MyTimer.WasTimerRunningBeforeClosing(this, EnumAlarmTypes.MinigameT1.ordinal)){//Timer was running. So, resume
            Log.d("MyTag","Found that timer was running before. Resuming")

            /*wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock( /*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON*/  FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, getString(R.string.WakeLockTagVal)).apply {
                    acquire(1 * 60 * 1000L)//1 minute timeout
                    Log.d("MyTag","Wake lock acquired")
                }
            }*/

            // this.scores= restoreScoreFromPref()//done from one place
            //VERY IIMPORTANT. Get appropriate destination id by looking at the pref value.
            val fragmentIDtoGo: Int =
                MyTimer.ResumeTimerBasedOnPref(this, EnumAlarmTypes.valueOf("MinigameT1"))

            if(fragmentIDtoGo.equals(R.id.minigameT1R1)){
                val bundle = Bundle()
                bundle.putBoolean("causeReminder", false)
                supportFragmentManager.primaryNavigationFragment?.findNavController()
                    ?.navigate(fragmentIDtoGo,bundle)
            }
            else{
                supportFragmentManager.primaryNavigationFragment?.findNavController()
                    ?.navigate(fragmentIDtoGo)
            }

            val nodesCount = MyTimer.selectedSyncNodesIds.size
            if(MyTimer.selectedSyncNodesIds.contains(MyTimer.nodeId) && nodesCount > 1){
                if(MyTimer.doContinue) {
                    MyTimer.doContinue = false
                    MyTimer.continueRemExit(this)
                }
                if(MyTimer.doShow){
                    MyTimer.doShow = false
                    MyTimer.showRemEntry(this)
                }
            }
        }
        else
            MyTimer.initScoreAndReminderTimes(this)
        val currentNavHost = supportFragmentManager.primaryNavigationFragment as NavHostFragment
        //if(currentNavHost != null) {
            if (currentNavHost.navController.currentDestination?.label == "Minigame T1 S1")//Timer is shown only in this fragment
            {
                MyTimer.resetAndupdate(this)
            }
        //}
    }
    
    public override fun onPause() {
        super.onPause()

        //MyApplication.activityPaused()
        Log.d("MyTag", "Minigame T1 On Pause method")
        if (MyTimer.currentState != EnumStateType.ALL_INACTIVE.ordinal){// IsMinigameActive(EnumAlarmTypes.MinigameT1.ordinal)){
            Log.d("MyTag","Stopping timer and setting alarm.")
            MyTimer.StopTimerAndSetAlarm(EnumAlarmTypes.valueOf("MinigameT1"),this)
        }
        /*else{
            MyTimer.SaveValues(this)
        }*/
        /*if(this::wakeLock.isInitialized){
            if (wakeLock.isHeld())
            {
                wakeLock.release();
                Log.d("MyTag","Wake lock released")
            }
        }*/
        //val algosIds = AlgosIds.getInstance(this)
        //algosIds.saveIds()

    }

    class ReminderBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Build and show the notification
            val notificationIntent = Intent(context, MinigameT1::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                .setContentTitle("Reminder")
                .setContentText("It's time for your activity!")
                .setSmallIcon(R.drawable.robot) // Replace with your app's icon.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notification)
        }
    }

    /*private fun showTimerRunningNotification(title:String, content:String) {
        val notificationIntent = Intent(this, MinigameT1::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.avatar) // Replace with your app's icon.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(3, notification)
    }*/

}