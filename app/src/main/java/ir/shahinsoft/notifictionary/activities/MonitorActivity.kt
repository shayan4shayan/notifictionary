package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.activity_monitor.*

class MonitorActivity : BaseActivity() {

    lateinit var info: LiveData<List<WorkInfo>>

    val observer = Observer<List<WorkInfo>> {
        writeLog("works count: " + it.size)
        writeLog("\n")
        it.forEach { info ->
            writeLog("work status: id : ${info.id} , state : ${info.state}")
            writeLog("\n")
            writeLog("tags: ${info.tags}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        info = WorkManager.getInstance(this).getWorkInfosByTagLiveData("smartNotification")

        info.observeForever(observer)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        info.removeObserver(observer)
    }

    private fun writeLog(txt: String) {
        Log.d("MonitorActivity", txt)

        val content = log.text.toString()
        log.text = content.plus(txt)

    }
}