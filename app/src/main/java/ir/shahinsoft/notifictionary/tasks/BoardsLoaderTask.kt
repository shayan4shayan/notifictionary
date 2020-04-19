package ir.shahinsoft.notifictionary.tasks

import android.os.AsyncTask
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate

class BoardsLoaderTask(private val db : DatabaseWrapper, val listener:(List<Board>)->Unit) :
        AsyncTask<Int,Int,List<Board>>(){
    override fun doInBackground(vararg params: Int?): List<Board> {
        val boards = db.selectCategories()
        boards.forEach {
            it.totalCount = db.selectAllTranslates(it.id).size
        }
        return boards
    }


    override fun onPostExecute(result: List<Board>?) {
        listener(result!!)
    }


}