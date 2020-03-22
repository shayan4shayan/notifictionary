package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board

class InsertFavoriteCategoryTask(val db: DatabaseWrapper) : AsyncTask<Board, Int, Unit>() {
    override fun doInBackground(vararg params: Board?) {
        var lastid = db.lastCategoryId()
        params[0]!!.id = ++lastid
        db.insert(params[0]!!)
    }
}