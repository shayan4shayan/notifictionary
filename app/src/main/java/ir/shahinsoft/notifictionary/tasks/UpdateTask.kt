package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Translate

class UpdateTask(private val databaseWrapper: DatabaseWrapper) : AsyncTask<Translate,Int,Boolean>(){
    override fun doInBackground(vararg params: Translate?): Boolean {
        if (params.isEmpty()) return false
        databaseWrapper.update(params[0]!!)
        return true
    }
}