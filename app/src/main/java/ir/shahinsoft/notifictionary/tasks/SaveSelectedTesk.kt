package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate

class SaveSelectedTesk(val map: HashMap<Board, ArrayList<Translate>>, val db: DatabaseWrapper, val listener: OnFinishListener)
    : AsyncTask<Int, Int, Int>() {
    override fun doInBackground(vararg params: Int?): Int {

        map.keys.forEach { category ->
            if (category.id != -1)
                db.insert(category)
            val catId = db.lastCategoryId()
            map[category].apply {
                this!!.forEach { translate ->
                    if (category.id != -1)
                        translate.catId = catId
                    else translate.catId = -1
                }
                var id = db.lastTranslateId()
                forEach { it.id = ++id }
                db.insert(this)
            }
        }
        return 0
    }

    override fun onPostExecute(result: Int?) {
        listener.onSaveFinished()
    }


    interface OnFinishListener {
        fun onSaveFinished()
    }
}