package com.incentives.prototype

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.incentives.prototype.minigameT1.MyTimer
import java.util.concurrent.ExecutionException

class GetLocalNodeId     //Constructor///
    (var context: Context) : Thread() {
    //Send the message via the thread. This will send the message to all the currently-connected devices//
    override fun run() {
        val localNodeTask = Wearable.getNodeClient(context).localNode
        try {
            val localNode = Tasks.await(localNodeTask)
            MyTimer.nodeId = localNode.id
        } catch (exception: ExecutionException) {
        } catch (exception: InterruptedException) {
        }
    }
}
