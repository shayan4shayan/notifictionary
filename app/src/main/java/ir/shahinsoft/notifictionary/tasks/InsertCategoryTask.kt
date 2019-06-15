package ir.shahinsoft.notifictionary.tasks

import android.graphics.Color
import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category

class InsertCategoryTask(private val databaseWrapper: DatabaseWrapper, val onInsert: ((Category) -> Unit)? = null) : AsyncTask<String, Int, Category>() {
    override fun doInBackground(vararg params: String?): Category {
        val id = databaseWrapper.lastCategoryId() + 1
        val category = Category(params[0], id).apply { color = Color.WHITE }
        databaseWrapper.insert(category)
        return category
    }

    override fun onPostExecute(result: Category?) {
        if (result != null && onInsert != null)
            onInsert.invoke(result)
    }
}