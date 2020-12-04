package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Translate

class LoadRecentTask(private val wrapper: DatabaseWrapper, private val onResult: (List<Translate>) -> Unit) : AsyncTask<Unit, Unit, List<Translate>>() {
    override fun doInBackground(vararg params: Unit?): List<Translate> {
        return wrapper.selectAllTranslates().filterIndexed { index, _ -> index < 8 }.reversed()
    }

    override fun onPostExecute(result: List<Translate>?) {
        result?.apply(onResult) ?: onResult(emptyList());
    }
}