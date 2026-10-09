package com.incentives.prototype

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.ExecutionException

class SendMessage     //Constructor///
    (var path: String, var message: String, var context: Context) : Thread() {
    //Send the message via the thread. This will send the message to all the currently-connected devices//
    override fun run() {
        val nodeListTask = Wearable.getNodeClient(context).connectedNodes
        //val localNodeTask = Wearable.getNodeClient(context).localNode
        try {
            val nodes = Tasks.await(nodeListTask)
            //val localNode = Tasks.await(localNodeTask)
            for (node in nodes) {
                val sendMessageTask = Wearable.getMessageClient(context)
                    .sendMessage(node.id, path, message.toByteArray())
                try {
                    val result = Tasks.await(sendMessageTask)
                } catch (exception: ExecutionException) {
                } catch (exception: InterruptedException) {
                }
            }
        } catch (exception: ExecutionException) {
        } catch (exception: InterruptedException) {
        }
    }
}
