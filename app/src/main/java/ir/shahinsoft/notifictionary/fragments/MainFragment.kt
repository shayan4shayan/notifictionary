package ir.shahinsoft.notifictionary.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ir.shahinsoft.notifictionary.widget.NewCategoryDialog
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.adapters.MainPagerAdapter
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.LoadCategoryTask
import ir.shahinsoft.notifictionary.widget.GetNameDialog
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout.setSelectedTabIndicatorColor(getApplicationColor()!!)
        newCategory.tint(R.color.black)
        newCategory.setOnClickListener {
            addNewCategory()
        }
    }

    private val onCategorySelected = object : NewCategoryDialog.OnCategoryInsertListener {
        override fun onInsert(category: Category) {
            val adapter = viewPager.adapter as MainPagerAdapter
            adapter.categories.add(category)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addNewCategory() {
        NewCategoryDialog(context!!, onCategorySelected).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LoadCategoryTask(context.getAppDatabase()) { onDisplayViewPager(it) }.execute()
    }

    override fun onResume() {
        super.onResume()
        LoadCategoryTask(context?.getAppDatabase()!!) {
            checkForUpdate(it)
        }.execute()
    }

    private fun checkForUpdate(categories: ArrayList<Category>) {
        val adapter = viewPager.adapter as MainPagerAdapter
        adapter.categories.clear()
        adapter.categories.addAll(generateAllCategory(categories))
        adapter.notifyDataSetChanged()
    }

    private fun onDisplayViewPager(categories: ArrayList<Category>) {
        viewPager.visibility = View.VISIBLE
        tabLayout.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        val adapter = MainPagerAdapter(generateAllCategory(categories), childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(adapter)
    }

    private fun generateAllCategory(categories: ArrayList<Category>): ArrayList<Category> {
        val allCategory = Category(getString(R.string.all), ALL_CATEGORY_ID)
        categories.add(0, allCategory)
        return categories
    }

    private var isColorWhite = true

    fun changeTablayoutToPrimary() {
        if (isColorWhite) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val transition = layoutTop.background as TransitionDrawable
                transition.startTransition(300)
                transition.setDrawable(1, ColorDrawable().apply { color = getApplicationColor()!! })
            } else {
                layoutTop.setBackgroundColor(getApplicationColor()!!)
            }
            tabLayout.setTabTextColors(context?.getColorCompat(R.color.white)!!, context?.getColorCompat(R.color.white)!!)
            tabLayout.setSelectedTabIndicatorColor(context?.getColorCompat(R.color.white)!!)
            newCategory.tint(R.color.white)
            isColorWhite = false
        }
    }

    private fun getApplicationColor() = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_theme",ContextCompat.getColor(context!!,R.color.picker_red).toString())!!.toInt()


    fun changeTablayoutToWhite() {
        if (isColorWhite) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val transition = layoutTop.background as TransitionDrawable
            transition.reverseTransition(150)
        } else {
            layoutTop.setBackgroundColor(Color.WHITE)
        }
        tabLayout.setTabTextColors(context?.getColorCompat(R.color.black)!!, context?.getColorCompat(R.color.black)!!)
        tabLayout.setSelectedTabIndicatorColor(getApplicationColor()!!)
        newCategory.tint(R.color.black)
        isColorWhite = true
    }

    fun getCatId(): Int {
        val index = viewPager.currentItem
        return (viewPager.adapter as MainPagerAdapter).categories[index].id
    }

    fun addTranslateToCurrentCategory(translate: Translate) {
        (viewPager.adapter as MainPagerAdapter).fragments[viewPager.currentItem].onNewTranslate(translate)
    }
}