package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.databinding.DialogYesNoBinding

/**
 * Created by shayan4shayan on 4/6/18.
 */
class YesNoDialog(context: Context, private val returnItem: Any, private val listener: OnClickListener) : AlertDialog(context) {
    lateinit var binding : DialogYesNoBinding
    fun show(title: String, content: String) {
        super.show()
        binding = DialogYesNoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textTitle.text = title
        binding.textContent.text = content
        binding.btnNo.setOnClickListener { listener.onNoClicked(returnItem); dismiss() }
        binding.btnYes.setOnClickListener { listener.onYesClicked(returnItem); dismiss() }
    }


    interface OnClickListener {
        fun onYesClicked(item: Any)
        fun onNoClicked(item: Any)
    }
}