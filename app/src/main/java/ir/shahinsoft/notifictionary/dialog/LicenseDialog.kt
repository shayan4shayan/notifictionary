package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.dialog_licence.*

class LicenseDialog(context: Context, val acceptListener: () -> Unit, val exitListener: () -> Unit) : AlertDialog(context) {
    override fun show() {
        super.show()
        setContentView(R.layout.dialog_licence)
        accept.setOnClickListener { acceptListener() }
        exit.setOnClickListener { exitListener() }
    }
}