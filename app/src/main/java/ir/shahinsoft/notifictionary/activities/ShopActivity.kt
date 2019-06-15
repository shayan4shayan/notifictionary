package ir.shahinsoft.notifictionary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.ShopCategoryAdapter
import ir.shahinsoft.notifictionary.model.ShopCategory
import ir.shahinsoft.notifictionary.toast
import kotlinx.android.synthetic.main.activity_shop.*

class ShopActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        recycler.adapter = ShopCategoryAdapter(::onCategoryClicked)
        updateVisibility()
        fetchDataFromServer()
    }

    private fun fetchDataFromServer() {
        //todo :: send a GET request to server and get category data
    }

    private fun updateVisibility() {
        if (hasVisibleItems()) {
            progress.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            progress.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }

    fun nothingToShow() {
        toast(R.string.shop_still_not_working)
        finish()
    }

    private fun hasVisibleItems(): Boolean {
        (recycler.adapter as ShopCategoryAdapter).apply {
            return items.isNotEmpty()
        }
    }

    fun onCategoryClicked(item: ShopCategory) {
        //todo :: start new activity for displaying shopping items
    }

    fun displayList(items: List<ShopCategory>) {
        (recycler.adapter as ShopCategoryAdapter).apply {
            this.items.clear()
            this.items.addAll(items)
            notifyDataSetChanged()
        }
        updateVisibility()
        if (items.isEmpty()) {
            nothingToShow()
        }
    }
}
