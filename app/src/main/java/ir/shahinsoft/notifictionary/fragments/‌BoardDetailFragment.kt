package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.shahinsoft.notifictionary.databinding.FragmentBoardDetailBinding
import ir.shahinsoft.notifictionary.model.Board
import java.util.*

class BoardDetailFragment : Fragment() {

    var learnFragment : ((Board)->Unit)? = null

    lateinit var board: Board

    lateinit var binding : FragmentBoardDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBoardDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.text = board.name.toUpperCase(Locale.US)

        binding.learn.setOnClickListener {
            learnFragment?.apply {
                if(board.totalCount>0) {
                    this(board)
                }
            }
        }
    }
}