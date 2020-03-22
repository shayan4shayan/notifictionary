package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.InsertCategoryTask
import ir.shahinsoft.notifictionary.tasks.LoadCategoryTask
import kotlinx.android.synthetic.main.dialog_new_category.*

/**
 * Created by shayan4shayan on 4/6/18.
 */
class NewCategoryDialog(context: Context, val listener: OnCategoryInsertListener) : AlertDialog(context) {

    private lateinit var boards: List<Board>
    override fun show() {
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        super.show()
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        setContentView(R.layout.dialog_new_category)
        LoadCategoryTask(context.getAppDatabase()) { list ->
            boards = list
            btnSelect.setOnClickListener { newCategory() }
        }.execute()
    }

    private fun newCategory() {
        val cName = edit.text.toString()
        if (cName.isIn(boards)) {
            edit.error = context.getString(R.string.error_already_exists)
        } else {
            InsertCategoryTask(context.getAppDatabase()) {
                listener.onInsert(it)
                dismiss()
            }.execute(cName)
        }
    }

    interface OnCategoryInsertListener {
        fun onInsert(board: Board)
    }


    private fun String.isIn(boards: List<Board>): Boolean {
        return (0 until boards.size).map { boards[it].name.toLowerCase() }.contains(this.toLowerCase())
    }
}

