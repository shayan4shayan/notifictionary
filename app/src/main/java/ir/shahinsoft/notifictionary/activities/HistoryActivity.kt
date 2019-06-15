package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.HistoryAdapter
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.History
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.LoadHistoryTask
import kotlinx.android.synthetic.main.activity_history.*
import kotlin.concurrent.thread

class HistoryActivity : BaseActivity(), HistoryAdapter.OnItemActionClickListener {
    override fun onDelete(t: History) {
        thread {
            getAppDatabase().delete(t)
        }
        items.removeAt(0)
        recycler.adapter?.notifyItemRemoved(0)
        recycler.layoutManager?.scrollToPosition(0)
    }

    override fun onInsert(t: History) {
        thread {
            val id = getAppDatabase().lastTranslateId() + 1
            getAppDatabase().insert(t.toTranslate().apply { this.id = id })
        }
    }

    lateinit var items: ArrayList<History>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getText(R.string.history)

        LoadHistoryTask(getAppDatabase()) {
            items = it
            val adapter = HistoryAdapter(it, this)

            recycler.adapter = adapter
        }.execute()


        val manager = recycler.layoutManager as LinearLayoutManager
        manager.reverseLayout = true
        manager.stackFromEnd = true
        recycler.itemAnimator = DefaultItemAnimator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else if (item?.itemId == R.id.menu_clear) {
            clearAll()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun clearAll() {
        removeAllItemsInDatabase()
        clearList()
    }

    private fun clearList() {
        (recycler.adapter as HistoryAdapter).clearAll()
    }

    private fun removeAllItemsInDatabase() {
        thread {
            getAppDatabase().deleteHistories()
        }
    }
}
