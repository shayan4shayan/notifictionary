package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper

class CountLearnTask(val db: DatabaseWrapper, val id: Int) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg params: Unit?) {
        val word = db.select(id)
        word.correctCount++
        db.update(word)
    }
}