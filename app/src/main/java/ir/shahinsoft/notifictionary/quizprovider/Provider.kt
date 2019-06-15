package ir.shahinsoft.notifictionary.quizprovider

import ir.shahinsoft.notifictionary.model.Quiz
import ir.shahinsoft.notifictionary.model.Translate
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by shayan4shayan on 2/2/18.
 */
class Provider {
    fun getQuiz(id: Int, words: ArrayList<Translate>?): Quiz {
        val quiz = Quiz()
        val word = getWordWithId(id, words)
        words?.remove(word)
        val ws: ArrayList<Translate> = ArrayList(getSimilar(word, words))
        val rand = Random()
        val list = ArrayList<Translate>()
        list.add(word!!)
        (0 until 3).forEach { addToList(ws, list, rand.nextInt(ws.size)) }
        //shuffling list
        for (i in 0 until list.size) {
            val randomPosition = rand.nextInt(list.size)
            val tmp: Translate = list[i]
            list[i] = list[randomPosition]
            list[randomPosition] = tmp
        }
        words?.add(word)

        quiz.word = word
        quiz.r1 = list[0]
        quiz.r2 = list[1]
        quiz.r3 = list[2]
        quiz.r4 = list[3]
        return quiz
    }

    private fun addToList(ws: ArrayList<Translate>, list: ArrayList<Translate>, nextInt: Int) {
        list.add(ws.removeAt(nextInt))
    }

    private fun getWordWithId(id: Int, words: List<Translate>?): Translate? {
        return words?.find { it.id == id }
    }

    private fun getSimilar(word: Translate?, words: List<Translate>?): List<Translate> {
        return words?.sortedBy { Cosine().getSimilarity(word!!, it) }!!.subList(0, when {words.size > 10 -> 10
            else -> words.size
        })
    }

}