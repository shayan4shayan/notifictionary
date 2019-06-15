package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.dialog_yes_no.*

/**
 * Created by shayan4shayan on 4/6/18.
 */
class YesNoDialog(context: Context, private val returnItem: Any, private val listener: OnClickListener) : AlertDialog(context) {
    fun show(title: String, content: String) {
        super.show()
        setContentView(R.layout.dialog_yes_no)
        textTitle.text = title
        textContent.text = content
        btnNo.setOnClickListener { listener.onNoClicked(returnItem); dismiss() }
        btnYes.setOnClickListener { listener.onYesClicked(returnItem); dismiss() }
    }


    interface OnClickListener {
        fun onYesClicked(item: Any)
        fun onNoClicked(item: Any)
    }
}