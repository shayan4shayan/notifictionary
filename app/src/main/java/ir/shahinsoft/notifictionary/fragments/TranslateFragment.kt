package ir.shahinsoft.notifictionary.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.dialog.SelectLanguageDialog
import ir.shahinsoft.notifictionary.model.TranslateLanguage
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import kotlinx.android.synthetic.main.fragment_translate.*
import java.util.*

class TranslateFragment : Fragment(), Translator.TranslateListener {

    lateinit var source: String
    lateinit var target: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_translate, container, false)
    }

    private val wordTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            if (p0?.isEmpty() == true)
                return
            Handler().postDelayed({
                if (textWord?.text == p0) {
                    translateWord(textWord.text.toString())
                }
            }, 500)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initTranslationLanguages()

        speak.setOnClickListener {
            if (textWord.text.isNotEmpty()) {
                context?.startService(Intent(context, NotifictionaryService::class.java).apply {
                    action = ACTION_SPEAK
                    putExtra(EXTRA_WORD, textWord.text.toString())
                })
            }
        }

        sourceLanguage.setOnClickListener {
            getSourceFromUser()
        }

        targetLanguage.setOnClickListener {
            getTargetFromUser()
        }

        swapLanguages.setOnClickListener {
            swapLanguages()
        }

        textWord.addTextChangedListener(wordTextWatcher)
    }

    private fun swapLanguages() {
        val source = PreferenceManager.getDefaultSharedPreferences(context).getString("source", "en")
        val sourceName = PreferenceManager.getDefaultSharedPreferences(context).getString("sourceName", "english")

        val target = PreferenceManager.getDefaultSharedPreferences(context).getString("target", "fa")
        val targetName = PreferenceManager.getDefaultSharedPreferences(context).getString("targetName", "persian")

        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("source", target).putString("sourceName", targetName)
                .putString("target", source).putString("targetName", sourceName).apply()

        initTranslationLanguages()
    }

    private fun getTargetFromUser() {
        context?.apply {
            SelectLanguageDialog(this) {
                updateTargetLanguage(it)
            }.show()
        }
    }

    private fun updateSourceLanguage(it: TranslateLanguage) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("source", it.get())
                .putString("sourceName", it.toString()).apply()
        initTranslationLanguages()
    }

    private fun getSourceFromUser() {
        context?.apply {
            SelectLanguageDialog(this) {
                updateSourceLanguage(it)
            }.show()
        }
    }

    private fun updateTargetLanguage(it: TranslateLanguage) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("target", it.get())
                .putString("targetName", it.toString()).apply()
        initTranslationLanguages()
    }

    private fun initTranslationLanguages() {
        source = PreferenceManager.getDefaultSharedPreferences(context).getString("source", "en")
                ?: "en"
        target = PreferenceManager.getDefaultSharedPreferences(context).getString("target", "fa")
                ?: "fa"

        sourceLanguage.text = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("sourceName", "english")
                ?.toUpperCase(Locale.US) ?: "ENGLISH"

        targetLanguage.text = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("targetName", "persian")
                ?.toUpperCase(Locale.US) ?: "PERSIAN"

    }

    private fun translateWord(word: String) {
        Translator.with(context).translateFrom(source).TranslateTo(target).callback(this).translate(word)
    }

    override fun onWordTranslated(translate: String?) {
        textTranslation.setText(translate)
    }

    override fun onFailedToTranslate(reason: String?) {
        context?.toast(reason ?: getString(R.string.error_unknown))
    }
}