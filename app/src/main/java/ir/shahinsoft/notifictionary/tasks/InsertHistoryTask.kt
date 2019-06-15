package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.History

class InsertHistoryTask(val db: DatabaseWrapper) : AsyncTask<History, Int, Unit>() {
    override fun doInBackground(vararg params: History?) {
        if (params.isEmpty()) return
        var id = db.lastHistoryId()
        params.forEach {
            it?.id = ++id
            db.insert(it!!)
        }
    }
}