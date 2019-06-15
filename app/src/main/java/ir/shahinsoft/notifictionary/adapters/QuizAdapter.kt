package ir.shahinsoft.notifictionary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ir.shahinsoft.notifictionary.model.Quiz
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.quizprovider.Provider

/**
 * Created by shayan4shayan on 4/13/18.
 */
class QuizAdapter(context: Context, private val words: List<Translate>,
                  private val allAvailableWords: ArrayList<Translate>, private val listener: OnQuestionAnsweredListener)
    : ArrayAdapter<Quiz>(context, R.layout.layout_quiz) {

    override fun getItem(position: Int): Quiz {
        return Provider().getQuiz(words[position].id, allAvailableWords)
    }

    override fun getPosition(item: Quiz?): Int {
        if (item == null) return 0
        return words.indexOf(words.filter { item.word?.name == it.name }[0])
    }

    override fun getCount(): Int {
        return words.size
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val quiz = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.layout_quiz, parent, false)
        val mQuiz = view.findViewById<TextView>(R.id.text_quiz)
        val mCorrect = view.findViewById<TextView>(R.id.text_correct_answers)
        val mWrong = view.findViewById<TextView>(R.id.text_wrong_answers)
        val t1 = view.findViewById<TextView>(R.id.t1)
        val t2 = view.findViewById<TextView>(R.id.t2)
        val t3 = view.findViewById<TextView>(R.id.t3)
        val t4 = view.findViewById<TextView>(R.id.t4)

        mQuiz.text = quiz.word?.name
        mCorrect.text = "${context.getString(R.string.correct_count)} : ${quiz.word?.correctCount}"
        mWrong.text = "${context.getString(R.string.wrong_count)} : ${quiz.word?.wrongCount}"

        t1.text = quiz.r1?.translate
        t2.text = quiz.r2?.translate
        t3.text = quiz.r3?.translate
        t4.text = quiz.r4?.translate

        t1.setOnClickListener(OnAnswerListener(listener,1,quiz))
        t2.setOnClickListener(OnAnswerListener(listener,2,quiz))
        t3.setOnClickListener(OnAnswerListener(listener,3,quiz))
        t4.setOnClickListener(OnAnswerListener(listener,4,quiz))
        return view
    }

    interface OnQuestionAnsweredListener {
        fun onCorrect(translate: Translate)
        fun onWrong(translate: Translate)
    }

    class OnAnswerListener(private val listener: OnQuestionAnsweredListener, private val pos: Int, private val quiz: Quiz) : View.OnClickListener {
        override fun onClick(v: View?) {
            if (quiz.isCurrect(pos)) {
                listener.onCorrect(quiz.word!!)
            } else {
                listener.onWrong(quiz.word!!)
            }
        }

    }
}