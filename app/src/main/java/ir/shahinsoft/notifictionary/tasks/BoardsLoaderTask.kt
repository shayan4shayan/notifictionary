package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate

class BoardsLoaderTask(private val db : DatabaseWrapper, val listener:(List<Board>)->Unit) :
        AsyncTask<Int,Int,List<Board>>(){
    override fun doInBackground(vararg params: Int?): List<Board> {
        return db.selectCategories()
    }


    override fun onPostExecute(result: List<Board>?) {
        listener(result!!)
    }


}