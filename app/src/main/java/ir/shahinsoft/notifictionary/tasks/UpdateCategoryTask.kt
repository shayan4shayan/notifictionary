package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category

class UpdateCategoryTask(val db : DatabaseWrapper) : AsyncTask<Category,Int,Unit>(){
    override fun doInBackground(vararg params: Category?) {
        if (params.isEmpty()) return
        params.forEach { db.update(it!!) }
    }

}