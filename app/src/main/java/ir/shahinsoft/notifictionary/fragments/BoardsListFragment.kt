package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.shahinsoft.notifictionary.adapters.CategoryAdapter
import ir.shahinsoft.notifictionary.databinding.FragmentCategoryListBinding
import ir.shahinsoft.notifictionary.dialog.AddBoardDialog
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask
import ir.shahinsoft.notifictionary.tasks.InsertCategoryTask

class BoardsListFragment : androidx.fragment.app.Fragment() {

    var forceUpdate = false

    var activityCallback : ((Board) -> Unit)? = null

    lateinit var binding : FragmentCategoryListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadAllBoards()

        binding.add.setOnClickListener {
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
        InsertCategoryTask(context?.getAppDatabase()!!) {
            forceUpdate = true
            loadAllBoards()
            forceUpdate = false
        }.execute(name)
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
        binding.recycler.adapter = CategoryAdapter(ArrayList(boards)){
            onBoardSelected(it)
        }
    }


}