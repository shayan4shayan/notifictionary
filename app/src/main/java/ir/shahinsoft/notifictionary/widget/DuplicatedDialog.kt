package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Translate
import kotlinx.android.synthetic.main.dialog_duplicated.*

class DuplicatedDialog(val translate: Translate, context: Context, val listener : OnDuplicatedSelectListener)
    : AlertDialog(context){
    override fun show() {
        super.show()
        setContentView(R.layout.dialog_duplicated)
        no.setOnClickListener { dismiss() }
        yes.setOnClickListener { dismiss() ; listener.onInsertDuplicated(translate) }
    }

    interface OnDuplicatedSelectListener{
        fun onInsertDuplicated(translate: Translate)
    }
}