package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate

class CategorizeLoaderTask(private val cats : List<Category>, private val db : DatabaseWrapper, val listener:OnLoadComplete) : AsyncTask<Int,Int,HashMap<Category,ArrayList<Translate>>>(){
    override fun doInBackground(vararg params: Int?): HashMap<Category,ArrayList<Translate>> {
        val map = HashMap<Category,ArrayList<Translate>>()
        (0 until cats.size)
                .map { cats[it] }
                .forEach { map[it] = db.selectAllTranslates(it.id) }
        return map
    }


    override fun onPostExecute(result: HashMap<Category, ArrayList<Translate>>?) {
        listener.onLoadCompleted(result!!)
    }

    interface OnLoadComplete{
        fun onLoadCompleted(map : HashMap<Category,ArrayList<Translate>>)
    }
}