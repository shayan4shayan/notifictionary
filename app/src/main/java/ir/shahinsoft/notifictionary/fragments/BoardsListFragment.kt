package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask
import kotlinx.android.synthetic.main.fragment_category_list.*

class BoardsListFragment : androidx.fragment.app.Fragment() {

    final val ALL = 0
    final val SHARED = 1
    final val RECENT = 2

    var displayType = ALL

    val onTabSelectListener= object :TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.apply {
                if (position==0){
                    if (displayType!=ALL){
                        loadAllBoards()
                    }
                } else if (position==1){
                    if (displayType!=RECENT){
                        loadRecentBoards()
                    }
                } else {
                    if (displayType!=SHARED){
                        loadSharedBoards()
                    }
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadAllBoards()

        tabLayout.addOnTabSelectedListener(onTabSelectListener)

        add.setOnClickListener {
            addBoard()
        }

    }

    private fun addBoard() {

    }

    private fun loadSharedBoards() {

    }

    private fun loadRecentBoards() {

    }

    fun onBoardSelected(board:Board){
        //TODO notify activity to update fragment
    }

    private fun loadAllBoards() {
        context?.apply {
            BoardsLoaderTask(getAppDatabase()){ boards ->
                displayBoards(boards)
            }.execute()
        }
    }

    private fun displayBoards(boards: List<Board>) {
        recycler.adapter = CategoryAdapter(ArrayList(boards)){
            onBoardSelected(it)
        }
    }


}