package ir.shahinsoft.notifictionary.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.ArrayAdapter
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.model.TranslateLanguage
import kotlinx.android.synthetic.main.activity_select_language.*
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class SelectLanguageActivity : BaseActivity(), TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = filter.text.toString()
        if (str.isEmpty()) {
            recycler.adapter = ArrayAdapter<TranslateLanguage>(this, R.layout.simple_text, list)
        } else {
            recycler.adapter = ArrayAdapter<TranslateLanguage>(this, R.layout.simple_text,
                    list.filter { it.toString().toLowerCase().contains(str) || it.get().contains(str) }
            )
        }
    }

    var list = ArrayList<TranslateLanguage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_language)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val titleRes = intent.getIntExtra("title", 0)
        if (titleRes > 0) {
            setTitle(titleRes)
        } else {
            finish()
        }
        val showAutoDetect = intent.getBooleanExtra("show", false)
        if (showAutoDetect) {
            val language = TranslateLanguage("Auto detect", "")
            list.add(language)
        }
        list.addAll(getLanguages())
        recycler.adapter = ArrayAdapter<TranslateLanguage>(this, R.layout.simple_text, list)
        recycler.setOnItemClickListener { _, _, position, _ -> onItemSelected(position) }
        filter.addTextChangedListener(this)
    }

    private fun onItemSelected(position: Int) {
        val str = filter.text.toString()
        val translate = list.filter { it.toString().toLowerCase().contains(str) || it.get().contains(str) }[position]
        val i = Intent()
        i.putExtra(NAME, translate.toString())
        i.putExtra(SHORT_NAME, translate.get())
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun getLanguages(): ArrayList<TranslateLanguage> {
        val `in` = resources.openRawResource(R.raw.support_languages)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item!!.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else
            super.onOptionsItemSelected(item)
    }

}
