package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.view.MenuItem
import ir.shahinsoft.languagenotifier.model.Help
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.HelpAdapter
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        title = getString(R.string.title_help)
        val helps = getHelps()
        helpRecycler.adapter = HelpAdapter(helps)
        val qa = getQAs()
        qaRecycler.adapter = HelpAdapter(qa)
        helpRecycler.adapter?.notifyDataSetChanged()
        qaRecycler.adapter?.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getQAs(): List<Help> {
        val list = ArrayList<Help>()
        list.add(Help(R.string.qa_title_change_notification_times, R.string.qa_how_to_change_notification_times))
        list.add(Help(R.string.qa_title_disable_notification, R.string.qa_how_to_disable_notifications))
        list.add(Help(R.string.qa_title_what_is_memorize_limit, R.string.qa_what_is_memorize_limit))
        list.add(Help(R.string.qa_title_how_to_save_my_words, R.string.qa_how_to_save_my_words))
        list.add(Help(R.string.qa_title_how_to_restore_my_saved_words, R.string.qa_how_to_restore_my_saved_words))
        list.add(Help(R.string.qa_title_why_import_and_export, R.string.qa_why_Import_and_export))
        list.add(Help(R.string.qa_title_how_take_quiz_works, R.string.qa_how_take_quiz_works))
        list.add(Help(R.string.qa_title_i_want_to_help_you_grow, R.string.qa_i_want_to_help_you_grow))
        list.add(Help(R.string.qa_title_other_questions, R.string.qa_other_questions))
        return list
    }

    private fun getHelps(): List<Help> {
        val list = ArrayList<Help>()
        list.add(Help(R.string.help_title_add_new_word, R.string.help_add_new_word))
        list.add(Help(R.string.help_title_add_a_new_category, R.string.help_add_a_new_category))
        list.add(Help(R.string.help_title_add_to_category, R.string.help_add_to_category))
        list.add(Help(R.string.help_title_change_translate, R.string.help_change_translate))
        list.add(Help(R.string.help_title_move_word_to_another_category, R.string.help_move_word_to_another_category))
        list.add(Help(R.string.help_title_edit_or_delete, R.string.help_edit_or_delete))
        return list
    }
}
