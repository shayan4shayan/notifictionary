package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board

class DeleteCategoryTask(val db: DatabaseWrapper, val onDeleteSuccess: () -> Unit) : AsyncTask<Board, Int, Unit>() {
    override fun doInBackground(vararg params: Board?) {
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