package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board

class UpdateCategoryTask(val db : DatabaseWrapper) : AsyncTask<Board,Int,Unit>(){
    override fun doInBackground(vararg params: Board?) {
        if (params.isEmpty()) return
        params.forEach { db.update(it!!) }
    }

}