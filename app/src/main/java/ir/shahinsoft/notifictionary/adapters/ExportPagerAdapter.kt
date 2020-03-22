package ir.shahinsoft.notifictionary.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ir.shahinsoft.notifictionary.fragments.ListFragment
import ir.shahinsoft.notifictionary.model.Board

class ExportPagerAdapter(val boards: List<Board>, fm: FragmentManager, private val provider: FragmentProvider) : FragmentPagerAdapter(fm) {
    internal val fragments = SparseArray<ListFragment>()
    override fun getItem(position: Int): Fragment {
        var fragment = fragments[position]
        if (fragment == null) {
            fragment = provider.getFragment()
            fragments.append(position, fragment)
        }
        fragment.board = boards[position]
        return fragment
    }

    override fun getCount(): Int {
        return boards.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return boards[position].name
    }


    interface FragmentProvider {
        fun getFragment(): ListFragment
    }
}

