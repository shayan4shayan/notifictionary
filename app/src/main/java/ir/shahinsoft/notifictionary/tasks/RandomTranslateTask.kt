package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Translate
import java.util.*
import kotlin.collections.ArrayList

class RandomTranslateTask(val db: DatabaseWrapper,val goal:Int, private val onLoadComplete: (Translate) -> Unit) : AsyncTask<Unit, Int, Translate>() {
    override fun doInBackground(vararg params: Unit?): Translate {
        val ids = db.translateIdsForReminder(goal)
        //if (ids.isEmpty()) return null
        val list = ArrayList<Int>()
        ids.forEach { list.add(it) }
        list.shuffle()
        val random = Random().nextInt(ids.size)
        val id = list[random]
        return db.select(id)
    }

    override fun onPostExecute(result: Translate) {
        onLoadComplete(result)
    }
}