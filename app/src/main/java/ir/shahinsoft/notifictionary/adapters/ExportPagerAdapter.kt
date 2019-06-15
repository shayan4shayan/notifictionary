package ir.shahinsoft.notifictionary.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ir.shahinsoft.notifictionary.fragments.ListFragment
import ir.shahinsoft.notifictionary.model.Category

class ExportPagerAdapter(val categories: List<Category>, fm: FragmentManager, private val provider: FragmentProvider) : FragmentPagerAdapter(fm) {
    internal val fragments = SparseArray<ListFragment>()
    override fun getItem(position: Int): Fragment {
        var fragment = fragments[position]
        if (fragment == null) {
            fragment = provider.getFragment()
            fragments.append(position, fragment)
        }
        fragment.category = categories[position]
        return fragment
    }

    override fun getCount(): Int {
        return categories.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return categories[position].name
    }


    interface FragmentProvider {
        fun getFragment(): ListFragment
    }
}

