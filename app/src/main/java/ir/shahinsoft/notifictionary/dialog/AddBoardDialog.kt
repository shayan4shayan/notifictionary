package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.dialog_new_category.*



class AddBoardDialog(context:Context, val callback : (String)->Unit) : AlertDialog(context){
    override fun show() {
        super.show()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        setContentView(R.layout.dialog_new_category)
        btnSelect.setOnClickListener {
            val name = edit.text.toString()
            if(name.isEmpty()){
                edit.error = "Name is Empty"
            } else {
                callback(name)
                dismiss()
            }
        }
    }
}