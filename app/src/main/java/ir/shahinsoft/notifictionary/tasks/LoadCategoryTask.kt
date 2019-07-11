package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category

class LoadCategoryTask(private val db: DatabaseWrapper, val onCategoryLoad: (ArrayList<Category>) -> Unit) : AsyncTask<Int, Int, ArrayList<Category>>() {
    override fun doInBackground(vararg params: Int?): ArrayList<Category> {
        return db.selectCategories()
        return ArrayList()
    }

    override fun onPostExecute(result: ArrayList<Category>?) {
        if (result != null) {
            onCategoryLoad(result)
        }
    }

}