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
import ir.shahinsoft.notifictionary.databinding.BottomSheetTranslateBinding
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.tasks.InsertTask
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class TranslateBottomSheet(context: Context, val isReadonly: Boolean, val text: String, val onDismiss: (String) -> Unit)
    : BottomSheetDialog(context) {

    lateinit var binding : BottomSheetTranslateBinding

    private val onLanguageChanged = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val target = binding.spinnerTarget.selectedItem as String
            translateTo(target)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private val translateListener = object : Translator.TranslateListener {
        override fun onWordTranslated(translate: String?) {
            binding.textTranslate.text = translate
            binding.textTranslate.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
            binding.textTranslate.isClickable = false
            if (!isReadonly) {
                binding.replace.isEnabled = true
            }
            binding.addToTranslateList.visibility = View.VISIBLE
        }

        override fun onFailedToTranslate(reason: String?) {
            binding.textTranslate.text = reason ?: "cannot translate Tap to retry!!"
            binding.textTranslate.isClickable = true
            binding.textTranslate.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
            binding.replace.isEnabled = false
        }

    }

    private fun translateTo(target: String) {
        binding.textTranslate.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        Translator.with(context).TranslateTo(target).callback(translateListener).translate(text)
    }

    override fun show() {
        super.show()
        binding = BottomSheetTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCancelable(false)
        binding.textWord.text = text
        binding.layout.setBackgroundColor(getApplicationColor())
        val languages = getLanguages()
        binding.spinnerTarget.adapter = ArrayAdapter<String>(context, R.layout.simple_text_white, languages)
        binding.spinnerTarget.setPopupBackgroundDrawable(ColorDrawable(getApplicationColor()))
        binding.spinnerTarget.onItemSelectedListener = onLanguageChanged
        val target = context.getSharedPreferences(APP, MODE_PRIVATE).getString(TARGET_SMALL, "fa")
        val index = languages.indexOf(target ?: "fa")
        binding.spinnerTarget.setSelection(index)

        binding.textTranslate.setOnClickListener {
            translateTo(target ?: "fa")
        }
        translateTo(target ?: "fa")

        binding.replace.setOnClickListener {
            onDismiss(binding.textTranslate.text.toString())
            dismiss()
        }
        binding.close.setOnClickListener {
            onDismiss("")
            dismiss()
        }

        binding.speak.setOnClickListener {
            Intent(context, NotifictionaryService::class.java).apply {
                action = ACTION_SPEAK
                putExtra(EXTRA_WORD, text)
                context.startService(this)
            }
        }

        if (isReadonly) binding.replace.visibility = View.INVISIBLE

        binding.addToTranslateList.setOnClickListener { addToTranslateList() }
        binding.addToTranslateList.visibility = View.INVISIBLE
    }

    private fun addToTranslateList(){
        InsertTask(context.getAppDatabase()) {
            dismiss()
            onDismiss("")
        }.execute(getTranslate())
    }

    private fun getTranslate(): Translate {
        return Translate().apply {
            name = binding.textWord.text.toString()
            translate = binding.textTranslate.text.toString()
            lang = binding.spinnerTarget.selectedItem as String
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