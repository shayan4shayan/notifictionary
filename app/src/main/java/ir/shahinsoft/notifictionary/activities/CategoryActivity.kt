package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import ir.shahinsoft.notifictionary.widget.NewCategoryDialog
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.DeleteCategoryTask
import ir.shahinsoft.notifictionary.tasks.LoadCategoryTask
import ir.shahinsoft.notifictionary.tasks.UpdateCategoryTask
import ir.shahinsoft.notifictionary.widget.ColorPickerDialog
import ir.shahinsoft.notifictionary.widget.YesNoDialog
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : BaseActivity(), CategoryAdapter.OnRemoveListener, YesNoDialog.OnClickListener, NewCategoryDialog.OnCategoryInsertListener {
    override fun onCategorySelected(board: Board) {

    }

    override fun onSelectColor(cat: Board) {
        ColorPickerDialog(this) { color ->
            recycler.adapter?.notifyDataSetChanged()
            UpdateCategoryTask(getAppDatabase()).execute(cat)
        }.show()
    }

    override fun onInsert(board: Board) {
        list.add(0, board)
        recycler.adapter?.notifyItemInserted(0)
    }

    override fun onYesClicked(item: Any) {
        if (item is Board) {
            DeleteCategoryTask(getAppDatabase()) {
                Toast.makeText(this, R.string.category_deleted, Toast.LENGTH_LONG).show()
                val pos = list.indexOf(item)
                list.removeAt(pos)
                recycler.adapter?.notifyItemRemoved(pos)
            }.execute(item)
        }
    }

    override fun onNoClicked(item: Any) {

    }

    override fun onRemove(cat: Board) {
        YesNoDialog(this, cat, this)
                .show(getString(R.string.title_remove_category), getString(R.string.content_remove_category))
    }

    lateinit var list: ArrayList<Board>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.title_category)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_category, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_add) {
            NewCategoryDialog(this, this).show()
        } else if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
