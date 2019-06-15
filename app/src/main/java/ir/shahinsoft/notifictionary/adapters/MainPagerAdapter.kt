package ir.shahinsoft.notifictionary.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.util.SparseArray
import androidx.viewpager.widget.ViewPager
import ir.shahinsoft.notifictionary.CATEGORY_ID
import ir.shahinsoft.notifictionary.fragments.CategoryListFragment
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate

class MainPagerAdapter(val categories: ArrayList<Category>, fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm), ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        //fragments[position].onSelected()
    }

    val fragments = SparseArray<CategoryListFragment>()

    private val onWordMoveListener = object : CategoryListFragment.OnWordMoveListener {
        override fun onMove(translate: Translate, catId: Int) {
            (0 until fragments.size()).map { fragments.get(fragments.keyAt(it)) }.find { it.catId == catId }?.onWordInserted(translate)
        }
    }

    override fun getItem(p0: Int): androidx.fragment.app.Fragment {
        val category = categories[p0]
        if (fragments[p0] == null){
            fragments.put(p0, CategoryListFragment())
            fragments[p0].onWordMoveListener = onWordMoveListener
        }
        val fragment = fragments[p0]
        val bundle = Bundle()
        bundle.putInt(CATEGORY_ID, category.id)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount() = categories.size

    override fun getPageTitle(position: Int): CharSequence? {
        return categories[position].name
    }
}