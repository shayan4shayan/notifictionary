package ir.shahinsoft.notifictionary.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Translate
import kotlinx.android.synthetic.main.bottom_sheet_delete.*

class DeleteBottomSheet(context: Context,val translate: Translate, val onDelete: (Translate) -> Unit) : BottomSheetDialog(context) {
    override fun show() {
        super.show()
        setContentView(R.layout.bottom_sheet_delete)
        textDescription.text = String.format(context.getString(R.string.delete_description_format), translate.name)
        cancel.setOnClickListener { cancel() }
        delete.setOnClickListener { onDelete(translate) ; dismiss() }
    }
}