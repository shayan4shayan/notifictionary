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
import androidx.fragment.app.FragmentTransaction
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.databinding.FragmentTranslateBinding
import ir.shahinsoft.notifictionary.dialog.PickBoardDialog
import ir.shahinsoft.notifictionary.dialog.SelectLanguageDialog
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.model.TranslateLanguage
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.tasks.InsertTask
import java.util.*

class TranslateFragment : Fragment(), Translator.TranslateListener {

    lateinit var source: String
    lateinit var target: String

    lateinit var binding : FragmentTranslateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTranslateBinding.inflate(inflater,container,false)
        return binding.root
    }

    private val wordTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            if (p0?.isEmpty() == true)
                return
            Handler().postDelayed({
                if (binding.textWord?.text == p0) {
                    translateWord(binding.textWord.text.toString())
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

        binding.speak.setOnClickListener {
            if (binding.textWord.text.isNotEmpty()) {
                context?.startService(Intent(context, NotifictionaryService::class.java).apply {
                    action = ACTION_SPEAK
                    putExtra(EXTRA_WORD, binding.textWord.text.toString())
                })
            }
        }

        binding.sourceLanguage.setOnClickListener {
            getSourceFromUser()
        }

        binding.targetLanguage.setOnClickListener {
            getTargetFromUser()
        }

        binding.swapLanguages.setOnClickListener {
            swapLanguages()
        }

        binding.textWord.addTextChangedListener(wordTextWatcher)

        binding.add.setOnClickListener {
            if (binding.textWord.text.isEmpty() || binding.textTranslation.text.isEmpty()){
                return@setOnClickListener
            }
            addWordToBoard()
        }
    }

    private fun addWordToBoard() {
        context?.apply {
            PickBoardDialog(this) {
                addWordToBoard(it)
            }.show()
        }
    }

    private fun addWordToBoard(board: Board){
        val word = binding.textWord.text.toString()
        val translation = binding.textTranslation.text.toString()
        val translate = Translate()
        translate.name = word
        translate.translate = translation
        translate.catId = board.id
        InsertTask(context?.getAppDatabase()!!){
            binding.textWord.text.clear()
            binding.textTranslation.text.clear()
            context?.toast(String.format(getString(R.string.add_message_format),word,board.name))
        }.execute(translate)
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

        binding.sourceLanguage.text = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("sourceName", "english")
                ?.toUpperCase(Locale.US) ?: "ENGLISH"

        binding.targetLanguage.text = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("targetName", "persian")
                ?.toUpperCase(Locale.US) ?: "PERSIAN"

    }

    private fun translateWord(word: String) {
        Translator.with(context).translateFrom(source).TranslateTo(target).callback(this).translate(word)
    }

    override fun onWordTranslated(translate: String?) {
        binding.textTranslation.setText(translate)
    }

    override fun onFailedToTranslate(reason: String?) {
        context?.toast(reason ?: getString(R.string.error_unknown))
    }
}