package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask
import kotlinx.android.synthetic.main.dialog_pick_board.*

class PickBoardDialog(context:Context, val callback : (Board)->Unit) : AlertDialog(context){
    override fun show() {
        super.show()
        setContentView(R.layout.dialog_pick_board)
        loadBoards()
    }

    private fun loadBoards() {
        BoardsLoaderTask(context.getAppDatabase()){
            displayBoards(it)
        }.execute()
    }

    private fun displayBoards(boards:List<Board>) {
        recycler.adapter = CategoryAdapter(ArrayList(boards)) {
            callback(it)
            dismiss()
        }
    }
}