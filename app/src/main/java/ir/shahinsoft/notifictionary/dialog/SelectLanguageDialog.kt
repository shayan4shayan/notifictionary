package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.SelectLanguageAdapter
import ir.shahinsoft.notifictionary.model.TranslateLanguage
import kotlinx.android.synthetic.main.dialog_select_language.*
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class SelectLanguageDialog(context: Context, val listener: (TranslateLanguage) -> Unit) : AlertDialog(context) {
    override fun show() {
        super.show()
        setContentView(R.layout.dialog_select_language)
        recycler.adapter = SelectLanguageAdapter(getLanguages()) {
            listener(it)
            dismiss()
        }
    }

    private fun getLanguages(): ArrayList<TranslateLanguage> {
        val `in` = context.resources.openRawResource(R.raw.support_languages)
        val bos = ByteArrayOutputStream()
        try {
            val bytes = ByteArray(`in`.available())
            `in`.read(bytes)
            bos.write(bytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val languages = ArrayList<TranslateLanguage>()
        try {
            val array = JSONArray(bos.toString())
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                languages.add(TranslateLanguage(
                        `object`.getString("language"),
                        `object`.getString("code")
                ))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return languages
    }

}