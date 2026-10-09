package org.incentives.questiongame_v2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*

private const val COUNT_KEY = "com.example.key.count"

class MainActivity : AppCompatActivity() {
    private lateinit var dataClient: DataClient
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        dataClient = Wearable.getDataClient(this)
        increaseCounter()
    }
    private fun increaseCounter() {
        val putDataReq: PutDataRequest = PutDataMapRequest.create("/count").run {
            dataMap.putInt(COUNT_KEY, count++)
            asPutDataRequest()
        }
        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)
    }

    fun incCounter(view: View) {
        increaseCounter()
    }

}