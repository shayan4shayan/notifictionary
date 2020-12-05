package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.HistoryAdapter
import ir.shahinsoft.notifictionary.databinding.ActivityHistoryBinding
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.History
import ir.shahinsoft.notifictionary.tasks.LoadHistoryTask
import kotlin.concurrent.thread

class HistoryActivity : BaseActivity(), HistoryAdapter.OnItemActionClickListener {
    lateinit var binding : ActivityHistoryBinding
    override fun onDelete(t: History) {
        thread {
            getAppDatabase().delete(t)
        }
        items.removeAt(0)
        binding.recycler.adapter?.notifyItemRemoved(0)
        binding.recycler.layoutManager?.scrollToPosition(0)
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
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getText(R.string.history)

        LoadHistoryTask(getAppDatabase()) {
            items = it
            val adapter = HistoryAdapter(it, this)

            binding.recycler.adapter = adapter
        }.execute()


        val manager = binding.recycler.layoutManager as LinearLayoutManager
        manager.reverseLayout = true
        manager.stackFromEnd = true
        binding.recycler.itemAnimator = DefaultItemAnimator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun clearAll() {
        removeAllItemsInDatabase()
        clearList()
    }

    private fun clearList() {
        (binding.recycler.adapter as HistoryAdapter).clearAll()
    }

    private fun removeAllItemsInDatabase() {
        thread {
            getAppDatabase().deleteHistories()
        }
    }
}
