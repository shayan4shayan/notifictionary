package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Translate

class DeleteTranslateTask(val db: DatabaseWrapper) : AsyncTask<Translate, Int, Unit>() {
    override fun doInBackground(vararg params: Translate?) {
        if (params.isEmpty()) return
        params.forEach {
            db.delete(it!!)
        }
    }
}