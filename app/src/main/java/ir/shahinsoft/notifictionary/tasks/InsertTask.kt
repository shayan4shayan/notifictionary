package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Translate

class InsertTask(private val databaseWrapper: DatabaseWrapper, val onFinish: () -> Unit) : AsyncTask<Translate, Int, Boolean>() {
    override fun doInBackground(vararg params: Translate): Boolean {
        var id = databaseWrapper.lastTranslateId()
        params.forEach { it.id = ++id }
        databaseWrapper.insert(params)
        return true
    }

    override fun onPostExecute(result: Boolean?) {
        onFinish()
    }
}