package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.PhoneUsage

class LoadUsageTask(val database: DatabaseWrapper, val callback: (ArrayList<PhoneUsage>?) -> Unit) : AsyncTask<Unit, Unit, ArrayList<PhoneUsage>>() {
    override fun doInBackground(vararg params: Unit?): ArrayList<PhoneUsage> {
        return database.selectAllPhoneUsages()
    }

    override fun onPostExecute(result: ArrayList<PhoneUsage>?) {
        callback(result)
    }

}