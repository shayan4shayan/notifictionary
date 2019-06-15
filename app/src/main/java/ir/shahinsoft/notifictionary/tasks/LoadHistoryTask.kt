package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.History

class LoadHistoryTask(val db: DatabaseWrapper, val onLoadComplete: (ArrayList<History>) -> Unit) : AsyncTask<Int, Int, ArrayList<History>>() {
    override fun doInBackground(vararg params: Int?): ArrayList<History> {
        return db.getHistories()
    }

    override fun onPostExecute(result: ArrayList<History>?) {
        onLoadComplete(result ?: ArrayList())
    }
}