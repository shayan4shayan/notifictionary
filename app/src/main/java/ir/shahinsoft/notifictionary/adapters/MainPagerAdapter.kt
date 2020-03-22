package ir.shahinsoft.notifictionary.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.util.SparseArray
import ir.shahinsoft.notifictionary.CATEGORY_ID
import ir.shahinsoft.notifictionary.fragments.BoardsListFragment
import ir.shahinsoft.notifictionary.model.Board

class MainPagerAdapter(val boards: ArrayList<Board>, fm: FragmentManager) : FragmentPagerAdapter(fm){

    val fragments = SparseArray<BoardsListFragment>()

    override fun getItem(p0: Int): Fragment {
        val category = boards[p0]
        if (fragments[p0] == null){
            fragments.put(p0, BoardsListFragment())
        }
        val fragment = fragments[p0]
        val bundle = Bundle()
        bundle.putInt(CATEGORY_ID, category.id)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount() = boards.size

    override fun getPageTitle(position: Int): CharSequence? {
        return boards[position].name
    }
}