package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.shahinsoft.notifictionary.databinding.FragmentLearnBinding
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.LoadWordTask
import java.util.*

class LearnFragment : Fragment() {
    lateinit var board: Board

    lateinit var wordsStack : Stack<Translate>

    var right = 0
    var wrong = 0

    lateinit var binding : FragmentLearnBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLearnBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.finish.visibility = View.GONE
        binding.main.visibility = View.GONE
        binding.meaning.visibility = View.GONE

        context?.apply {
            LoadWordTask(getAppDatabase(),board.id){
                startLearning(it)
            }.execute()
        }

        binding.flip.setOnClickListener {
            displayTranslate()
        }

        binding.yes.setOnClickListener {
            right++
            if (wordsStack.empty()){
                displayFinish()
            } else {
                displayWord()
            }
        }

        binding.no.setOnClickListener {
            wrong ++
            if (wordsStack.empty()){
                displayFinish()
            } else{
                displayWord()
            }
        }

        binding.quit.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun startLearning(it: ArrayList<Translate>) {
        wordsStack = Stack()
        wordsStack.addAll(it.shuffled())

        displayWord()
    }

    fun displayFinish(){
        binding.main.visibility = View.GONE
        binding.meaning.visibility = View.GONE

        binding.finish.visibility = View.VISIBLE

        binding.rightText.text = "Right: $right"
        binding.wrongText.text = "Wrong: $wrong"
    }

    fun displayTranslate() {
        val translate = wordsStack.pop()
        binding.main.visibility = View.GONE

        binding.meaning.visibility = View.VISIBLE
        binding.textTranslate.text =translate.translate
    }

    fun displayWord(){
        val translate = wordsStack.peek()

        binding.main.visibility = View.VISIBLE
        binding.textWord.text = translate.name

    }
}