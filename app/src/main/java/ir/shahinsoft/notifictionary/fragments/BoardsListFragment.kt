package ir.shahinsoft.notifictionary.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.dialog.AddBoardDialog
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask
import ir.shahinsoft.notifictionary.tasks.InsertCategoryTask
import kotlinx.android.synthetic.main.fragment_category_list.*

class BoardsListFragment : androidx.fragment.app.Fragment() {

    final val ALL = 0
    final val SHARED = 1
    final val RECENT = 2

    var forceUpdate = false

    var displayType = ALL

    var activityCallback : ((Board) -> Unit)? = null

    val onTabSelectListener= object :TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            reloadBoards()
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
        context?.apply {
            AddBoardDialog(this) {
                insertNewBoard(it)
            }.show()
        }
    }
    private fun insertNewBoard(name:String){
        InsertCategoryTask(context!!.getAppDatabase()) {
            forceUpdate = true
            reloadBoards()
            forceUpdate = false
        }.execute(name)
    }

    private fun reloadBoards(){
        Log.i("BoardsListFragment","reloading boards")
        tabLayout?.apply {
            val position = selectedTabPosition
            Log.d("BoardsListFragment"," pos " + position)
            if (position==0){
                if (forceUpdate || displayType!=ALL){
                    loadAllBoards()
                }
            } else if (forceUpdate || position==1){
                if (displayType!=RECENT){
                    loadRecentBoards()
                }
            } else {
                if (forceUpdate || displayType!=SHARED){
                    loadSharedBoards()
                }
            }
        }
    }

    private fun loadSharedBoards() {

    }

    private fun loadRecentBoards() {

    }

    fun onBoardSelected(board:Board){
        Log.i("BoardListFragment","board ${board.name} selected")
        activityCallback?.apply {
            this(board)
        }
    }

    private fun loadAllBoards() {
        Log.d("BoardsListFragment", "loading boards")
        context?.apply {
            BoardsLoaderTask(getAppDatabase()){ boards ->
                displayBoards(boards)
                Log.d("BoardsListFragment",boards.toString())
            }.execute()
        }
    }

    private fun displayBoards(boards: List<Board>) {
        recycler?.adapter = CategoryAdapter(ArrayList(boards)){
            onBoardSelected(it)
        }
    }


}