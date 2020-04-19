package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.LoadWordTask
import kotlinx.android.synthetic.main.fragment_learn.*
import java.util.*

class LearnFragment : Fragment() {
    lateinit var board: Board

    lateinit var wordsStack : Stack<Translate>

    var right = 0
    var wrong = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        finish.visibility = View.GONE
        main.visibility = View.GONE
        meaning.visibility = View.GONE

        context?.apply {
            LoadWordTask(getAppDatabase(),board.id){
                startLearning(it)
            }.execute()
        }

        flip.setOnClickListener {
            displayTranslate()
        }

        yes.setOnClickListener {
            right++
            if (wordsStack.empty()){
                displayFinish()
            } else {
                displayWord()
            }
        }

        no.setOnClickListener {
            wrong ++
            if (wordsStack.empty()){
                displayFinish()
            } else{
                displayWord()
            }
        }

        quit.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun startLearning(it: ArrayList<Translate>) {
        wordsStack = Stack()
        wordsStack.addAll(it.shuffled())

        displayWord()
    }

    fun displayFinish(){
        main.visibility = View.GONE
        meaning.visibility = View.GONE

        finish.visibility = View.VISIBLE

        rightText.text = "Right: $right"
        wrongText.text = "Wrong: $wrong"
    }

    fun displayTranslate() {
        val translate = wordsStack.pop()
        main.visibility = View.GONE

        meaning.visibility = View.VISIBLE
        textTranslate.text =translate.translate
    }

    fun displayWord(){
        val translate = wordsStack.peek()

        main.visibility = View.VISIBLE
        textWord.text = translate.name

    }
}