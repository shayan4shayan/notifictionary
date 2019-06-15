package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.dialog_get_name.*

/**
 * Created by shayan4shayan on 2/20/18.
 */
class GetNameDialog(context: Context,val callback: Callback) : AlertDialog(context) {
    override fun show() {
        super.show()
        setContentView(R.layout.dialog_get_name)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        btnSelect.setOnClickListener { select() }
    }

    private fun select() {
        if (edit.text!!.isEmpty()) {
            Toast.makeText(context, R.string.error_file_name, Toast.LENGTH_LONG).show()
            return
        }
        callback.onSelect(edit.text.toString())
        dismiss()
    }

    interface Callback {
        fun onSelect(name: String)
    }
}