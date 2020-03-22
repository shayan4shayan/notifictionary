package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask

class BoardsListFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadAllBoards()
    }

    private fun loadAllBoards() {
        context?.apply {
            BoardsLoaderTask(getAppDatabase()){ boards ->
                displayBoards(boards)
            }.execute()
        }
    }

    private fun displayBoards(boards: List<Board>) {

    }


}