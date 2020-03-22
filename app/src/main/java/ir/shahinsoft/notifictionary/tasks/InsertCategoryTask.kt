package ir.shahinsoft.notifictionary.tasks

import android.graphics.Color
import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board

class InsertCategoryTask(private val databaseWrapper: DatabaseWrapper, val onInsert: ((Board) -> Unit)? = null) : AsyncTask<String, Int, Board>() {
    override fun doInBackground(vararg params: String?): Board {
        val id = databaseWrapper.lastCategoryId() + 1
        val category = Board(params[0], id)
        databaseWrapper.insert(category)
        return category
    }

    override fun onPostExecute(result: Board?) {
        if (result != null && onInsert != null)
            onInsert.invoke(result)
    }
}