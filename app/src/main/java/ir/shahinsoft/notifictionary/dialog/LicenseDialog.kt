package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.databinding.DialogLicenceBinding

class LicenseDialog(context: Context, val acceptListener: () -> Unit, val exitListener: () -> Unit) : AlertDialog(context) {
    lateinit var binding : DialogLicenceBinding
    override fun show() {
        super.show()
        binding = DialogLicenceBinding.inflate(layoutInflater)
        super.setCancelable(false)
        setContentView(binding.root)
        binding.accept.setOnClickListener { acceptListener() ; dismiss() }
        binding.exit.setOnClickListener { exitListener();dismiss() }
        setOnCancelListener { exitListener() }
    }
}