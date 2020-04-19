package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Board
import kotlinx.android.synthetic.main.fragment_board_detail.*
import java.util.*

class BoardDetailFragment : Fragment() {

    var learnFragment : ((Board)->Unit)? = null

    lateinit var board: Board


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        title.text = board.name.toUpperCase(Locale.US)

        learn.setOnClickListener {
            learnFragment?.apply {
                if(board.totalCount>0) {
                    this(board)
                }
            }
        }
    }
}