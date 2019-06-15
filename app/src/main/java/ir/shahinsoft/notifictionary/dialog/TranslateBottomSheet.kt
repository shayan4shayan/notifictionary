package ir.shahinsoft.notifictionary.dialog

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.tasks.InsertTask
import kotlinx.android.synthetic.main.bottom_sheet_translate.*
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class TranslateBottomSheet(context: Context, val isReadonly: Boolean, val text: String, val onDismiss: (String) -> Unit)
    : BottomSheetDialog(context) {

    private val onLanguageChanged = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val target = spinnerTarget.selectedItem as String
            translateTo(target)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private val translateListener = object : Translator.TranslateListener {
        override fun onWordTranslated(translate: String?) {
            textTranslate.text = translate
            textTranslate.visibility = View.VISIBLE
            progress.visibility = View.GONE
            textTranslate.isClickable = false
            if (!isReadonly) {
                replace.isEnabled = true
            }
            addToTranslateList.visibility = View.VISIBLE
        }

        override fun onFailedToTranslate(reason: String?) {
            textTranslate.text = reason ?: "cannot translate Tap to retry!!"
            textTranslate.isClickable = true
            textTranslate.visibility = View.VISIBLE
            progress.visibility = View.GONE
            replace.isEnabled = false
        }

    }

    private fun translateTo(target: String) {
        textTranslate.visibility = View.GONE
        progress.visibility = View.VISIBLE
        Translator.with(context).TranslateTo(target).callback(translateListener).translate(text)
    }

    override fun show() {
        super.show()
        setContentView(R.layout.bottom_sheet_translate)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCancelable(false)
        textWord.text = text
        layout.setBackgroundColor(getApplicationColor())
        val languages = getLanguages()
        spinnerTarget.adapter = ArrayAdapter<String>(context, R.layout.simple_text_white, languages)
        spinnerTarget.setPopupBackgroundDrawable(ColorDrawable(getApplicationColor()))
        spinnerTarget.onItemSelectedListener = onLanguageChanged
        val target = context.getSharedPreferences(APP, MODE_PRIVATE).getString(TARGET_SMALL, "fa")
        val index = languages.indexOf(target ?: "fa")
        spinnerTarget.setSelection(index)

        textTranslate.setOnClickListener {
            translateTo(target ?: "fa")
        }
        translateTo(target ?: "fa")

        replace.setOnClickListener {
            onDismiss(textTranslate.text.toString())
            dismiss()
        }
        close.setOnClickListener {
            onDismiss("")
            dismiss()
        }

        speak.setOnClickListener {
            Intent(context, NotifictionaryService::class.java).apply {
                action = ACTION_SPEAK
                putExtra(EXTRA_WORD, text)
                context.startService(this)
            }
        }

        if (isReadonly) replace.visibility = View.INVISIBLE

        addToTranslateList.setOnClickListener { addToTranslateList() }
        addToTranslateList.visibility = View.INVISIBLE
    }

    private fun addToTranslateList(){
        InsertTask(context.getAppDatabase()) {
            dismiss()
            onDismiss("")
        }.execute(getTranslate())
    }

    private fun getTranslate(): Translate {
        return Translate().apply {
            name = textWord.text.toString()
            translate = textTranslate.text.toString()
            lang = spinnerTarget.selectedItem as String
        }
    }

    private fun getLanguages(): ArrayList<String> {
        val `in` = context.resources.openRawResource(R.raw.support_languages)
        val bos = ByteArrayOutputStream()
        try {
            val bytes = ByteArray(`in`.available())
            `in`.read(bytes)
            bos.write(bytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val languages = ArrayList<String>()
        try {
            val array = JSONArray(bos.toString())
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                languages.add(
                        `object`.getString("code")
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return languages
    }

    private fun getApplicationColor() = context.getSharedPreferences(APP, Context.MODE_PRIVATE).getInt(THEME, ContextCompat.getColor(context, R.color.picker_red))

}