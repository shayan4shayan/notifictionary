package ir.shahinsoft.notifictionary.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.ArrayAdapter
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.LoadCategoryTask
import kotlinx.android.synthetic.main.bottom_sheet_edit.*

class EditBottomSheet(context: Context, val translate: Translate, val onEdit: (Translate) -> Unit) : BottomSheetDialog(context) {
    override fun show() {
        super.show()
        setContentView(R.layout.bottom_sheet_edit)
        initCategoriesSpinner()
        textWord.setText(translate.name)
        textTranslate.setText(translate.translate)
        spinnerType.setSelection(context.resources.getStringArray(R.array.word_types).indexOf(translate.type), true)
        cancel.setOnClickListener { cancel() }
        edit.setOnClickListener { edit() }
    }

    private fun initCategoriesSpinner() {
        LoadCategoryTask(context.getAppDatabase()) { categories ->
            val adapter = ArrayAdapter<Board>(context, android.R.layout.simple_dropdown_item_1line, categories)
            spinnerCategory.adapter = adapter
            spinnerCategory.setSelection(categories.indexOf(categories.find { it.id == translate.catId }), true)
        }.execute()
    }

    private fun edit() {
        val word = textWord.text.toString()
        val translate = textTranslate.text.toString()
        val category = spinnerCategory.selectedItem as Board
        val type = spinnerType.selectedItem as String
        if (word.isEmpty()) {
            layoutWord.error = context.getString(R.string.word_is_empty)
            return
        }
        if (translate.isEmpty()) {
            layoutTranslate.error = context.getString(R.string.translate_empty)
            return
        }
        this.translate.name = word
        this.translate.translate = translate
        this.translate.catId = category.id
        this.translate.type = type
        onEdit(this.translate)
        dismiss()
    }
}