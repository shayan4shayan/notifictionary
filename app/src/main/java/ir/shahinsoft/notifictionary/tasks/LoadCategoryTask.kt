package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board

class LoadCategoryTask(private val db: DatabaseWrapper, val onCategoryLoad: (ArrayList<Board>) -> Unit) : AsyncTask<Int, Int, ArrayList<Board>>() {
    override fun doInBackground(vararg params: Int?): ArrayList<Board> {
        return db.selectCategories()
        return ArrayList()
    }

    override fun onPostExecute(result: ArrayList<Board>?) {
        if (result != null) {
            onCategoryLoad(result)
        }
    }

}