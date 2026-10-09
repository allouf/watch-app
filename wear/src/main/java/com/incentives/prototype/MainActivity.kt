package com.incentives.prototype

import android.content.*
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.wearable.*
import com.incentives.prototype.minigameT1.MinigameT1
import com.incentives.prototype.minigameT1.MyTimer
import android.os.Build
import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import kotlinx.coroutines.*


class MainActivity :   FragmentActivity() {

    private var items: ArrayList<ItemsList> = ArrayList()

    public fun CoroutineScope.getNodeId() {
        launch(Dispatchers.IO) {
            GetLocalNodeId(applicationContext).start()
        }
    }

    private fun startMessageService() {
        val serviceIntent = Intent(this, MessageService::class.java)
        // Android 8.0 introduced startForegroundService() which should be used to start a Foreground Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMessageService()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = WearableLinearLayoutManager.VERTICAL

        recyclerView.layoutManager = layoutManager

        items.add(ItemsList(R.drawable.joystick, "Playground", "Playground"))
        items.add(ItemsList(R.drawable.sam, "AI Guide", "AI Guide"))
        items.add(ItemsList(R.drawable.settings2, "Performance Stats", "Performance Stats"))

        val listAdapter = ListAdapter(this.items) { v, itemPosition ->
            if (itemPosition == 0) {
                val intent = Intent(v.context, MinigameT1::class.java)
                startActivity(intent)
            } else if (itemPosition == 1) {
                val intent = Intent(v.context, DailyStatActivity::class.java)
                startActivity(intent)
            } else if (itemPosition == 2) {
                val intent = Intent(v.context, PerformanceStatActivity::class.java)
                startActivity(intent)
            }
        }

        recyclerView.adapter = listAdapter

        GlobalScope.getNodeId()


        MyTimer.initRemindersData(applicationContext)
        MyTimer.initializeReadyReminders()

        val layout_content = findViewById<ConstraintLayout>(R.id.contentMain)
        if (applicationContext.resources?.configuration?.isScreenRound == true) {
            val inset: Int = (0.1f * Resources.getSystem().displayMetrics.widthPixels).toInt()
            layout_content.setPadding(inset, 0, 0, 0)
        }
    }
}






