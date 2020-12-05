package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.databinding.DialogDuplicatedBinding
import ir.shahinsoft.notifictionary.model.Translate

class DuplicatedDialog(val translate: Translate, context: Context, val listener : OnDuplicatedSelectListener)
    : AlertDialog(context){
    lateinit var binding : DialogDuplicatedBinding
    override fun show() {
        super.show()
        binding = DialogDuplicatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.no.setOnClickListener { dismiss() }
        binding.yes.setOnClickListener { dismiss() ; listener.onInsertDuplicated(translate) }
    }

    interface OnDuplicatedSelectListener{
        fun onInsertDuplicated(translate: Translate)
    }
}