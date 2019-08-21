package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import ir.shahinsoft.notifictionary.widget.NewCategoryDialog
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.dialog.MergeDialog
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.tasks.DeleteCategoryTask
import ir.shahinsoft.notifictionary.tasks.LoadCategoryTask
import ir.shahinsoft.notifictionary.tasks.UpdateCategoryTask
import ir.shahinsoft.notifictionary.widget.ColorPickerDialog
import ir.shahinsoft.notifictionary.widget.YesNoDialog
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : BaseActivity(), CategoryAdapter.OnRemoveListener, YesNoDialog.OnClickListener, NewCategoryDialog.OnCategoryInsertListener {
    override fun onMerge(category: Category) {
        MergeDialog(this,category).show()
    }

    override fun onCategorySelected(category: Category) {

    }

    override fun onSelectColor(cat: Category) {
        ColorPickerDialog(this) { color ->
            cat.color = color
            recycler.adapter?.notifyDataSetChanged()
            UpdateCategoryTask(getAppDatabase()).execute(cat)
        }.show()
    }

    override fun onInsert(category: Category) {
        list.add(0, category)
        recycler.adapter?.notifyItemInserted(0)
    }

    override fun onYesClicked(item: Any) {
        if (item is Category) {
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

    override fun onRemove(cat: Category) {
        YesNoDialog(this, cat, this)
                .show(getString(R.string.title_remove_category), getString(R.string.content_remove_category))
    }

    lateinit var list: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.title_category)

        LoadCategoryTask(getAppDatabase()) {
            list = it
            recycler.itemAnimator = DefaultItemAnimator()
            val adapter = CategoryAdapter(list, this)
            recycler.adapter = adapter
            adapter.notifyDataSetChanged()
        }.execute()


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
