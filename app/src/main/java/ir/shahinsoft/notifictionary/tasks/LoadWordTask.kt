package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import android.util.Log
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Translate

class LoadWordTask(private val db: DatabaseWrapper, private val catId: Int?, val onLoadComplete: (ArrayList<Translate>) -> (Unit)) : AsyncTask<Int, Int, ArrayList<Translate>?>() {
    override fun doInBackground(vararg params: Int?): ArrayList<Translate>? {
        val page = if (params.isEmpty()) null else params[0]
        Log.d(javaClass.simpleName, "loading words from db with categoryId: $catId")
        if (page == null) return if (catId != null) db.selectAllTranslates(catId) else db.selectAllTranslates()

        return if (catId == null) {
            db.selectTranslates(page)
        } else {
            db.selectTranslates(catId, page)
        }
    }

    override fun onPostExecute(result: ArrayList<Translate>?) {
        onLoadComplete(result ?: ArrayList())

    }

}