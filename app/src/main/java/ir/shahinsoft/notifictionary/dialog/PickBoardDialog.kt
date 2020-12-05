package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.databinding.DialogPickBoardBinding
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask

class PickBoardDialog(context:Context, val callback : (Board)->Unit) : AlertDialog(context){
    lateinit var binding : DialogPickBoardBinding
    override fun show() {
        super.show()
        binding = DialogPickBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadBoards()
    }

    private fun loadBoards() {
        BoardsLoaderTask(context.getAppDatabase()){
            displayBoards(it)
        }.execute()
    }

    private fun displayBoards(boards:List<Board>) {
        binding.recycler.adapter = CategoryAdapter(ArrayList(boards)) {
            callback(it)
            dismiss()
        }
    }
}