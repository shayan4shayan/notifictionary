package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.PhoneUsage

class InsertUsageTast(private val databaseWrapper: DatabaseWrapper, val listener: (() -> Unit)? = null) :
        AsyncTask<PhoneUsage, Unit, Unit>() {
    override fun doInBackground(vararg params: PhoneUsage?) {
        if (params.isEmpty()) return
        params.forEach {
            if (it != null) {
                databaseWrapper.addPhoneUsage(it)
            }
        }
    }

    override fun onPostExecute(result: Unit?) {
        listener?.invoke()
    }

}