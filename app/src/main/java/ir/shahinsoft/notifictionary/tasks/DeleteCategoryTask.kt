package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category

class DeleteCategoryTask(val db: DatabaseWrapper, val onDeleteSuccess: () -> Unit) : AsyncTask<Category, Int, Unit>() {
    override fun doInBackground(vararg params: Category?) {
        if (params.isEmpty()) return
        params.forEach {
            db.deleteWordsByCategoryId(it!!.id)
            db.delete(it)
        }
    }

    override fun onPostExecute(result: Unit?) {
        onDeleteSuccess()
    }

}