package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.databinding.DialogNewCategoryBinding


class AddBoardDialog(context:Context, val callback : (String)->Unit) : AlertDialog(context){

    lateinit var binding : DialogNewCategoryBinding

    override fun show() {
        super.show()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding = DialogNewCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSelect.setOnClickListener {
            val name = binding.edit.text.toString()
            if(name.isEmpty()){
                binding.edit.error = "Name is Empty"
            } else {
                callback(name)
                dismiss()
            }
        }
    }
}