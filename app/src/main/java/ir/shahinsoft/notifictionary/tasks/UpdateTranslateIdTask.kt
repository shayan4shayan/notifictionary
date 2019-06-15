package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Translate

class UpdateTranslateIdTask(val databaseWrapper: DatabaseWrapper) : AsyncTask<Translate,Int,Boolean>(){
    override fun doInBackground(vararg params: Translate?): Boolean {
        params[0]?.id = databaseWrapper.lastTranslateId()
        return true
    }
}