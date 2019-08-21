package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category

class MergeTask(val db: DatabaseWrapper, val source: Category, val target: Category, val delete: Boolean,
                val listener: () -> Unit) : AsyncTask<Unit, Unit, Unit>() {

    override fun doInBackground(vararg params: Unit?) {
        db.selectAllTranslates(source.id).forEach {
            it.catId = target.id
            db.update(it)
        }
        if (delete) {
            db.delete(source)
        }
    }

    override fun onPostExecute(result: Unit?) {
        listener()
    }

}