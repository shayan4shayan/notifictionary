package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category

class InsertFavoriteCategoryTask(val db: DatabaseWrapper) : AsyncTask<Category, Int, Unit>() {
    override fun doInBackground(vararg params: Category?) {
        var lastid = db.lastCategoryId()
        params[0]!!.id = ++lastid
        db.insert(params[0]!!)
    }
}