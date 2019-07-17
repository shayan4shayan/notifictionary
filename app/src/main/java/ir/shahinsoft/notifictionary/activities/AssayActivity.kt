package ir.shahinsoft.notifictionary.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.yuyakaido.android.cardstackview.SwipeDirection
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.adapters.QuizAdapter
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.tasks.LoadWordTask
import ir.shahinsoft.notifictionary.tasks.UpdateTask
import ir.shahinsoft.notifictionary.tasks.UpdateTranslateIdTask
import kotlinx.android.synthetic.main.layout_assay.*
import kotlin.collections.ArrayList


@SuppressLint("Registered")
/**
 * Created by shayan on 11/18/2017.
 *
 */
class AssayActivity : BaseActivity(), QuizAdapter.OnQuestionAnsweredListener {
    override fun onCorrect(translate: Translate) {
        Log.d("Assay", "onCorrect called")
        swipeRight()
        correct++
    }

    private fun swipeRight() {
        val target = stackView.topView
        target.setOverlay(R.layout.layout_wrong, R.layout.layout_correct, 0, 0)
        val targetOverlay = stackView.topView.overlayContainer

        val rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", 10f))
        rotation.duration = 300
        val translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, 2000f))
        val translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f))
        translateX.startDelay = 200
        translateY.startDelay = 200
        translateX.duration = 700
        translateY.duration = 700
        val cardAnimationSet = AnimatorSet()
        cardAnimationSet.playTogether(rotation, translateX, translateY)

        val overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f)
        overlayAnimator.duration = 300
        val overlayAnimationSet = AnimatorSet()
        overlayAnimationSet.playTogether(overlayAnimator)

        stackView.swipe(SwipeDirection.Right, cardAnimationSet, overlayAnimationSet)
    }

    private fun swipeLeft() {
        val target = stackView.topView
        target.setOverlay(R.layout.layout_wrong, R.layout.layout_correct, 0, 0)
        val targetOverlay = stackView.topView.overlayContainer

        val rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", -10f))
        rotation.duration = 300
        val translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, -2000f))
        val translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f))
        translateX.startDelay = 200
        translateY.startDelay = 200
        translateX.duration = 700
        translateY.duration = 700
        val cardAnimationSet = AnimatorSet()
        cardAnimationSet.playTogether(rotation, translateX, translateY)

        val overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f)
        overlayAnimator.duration = 300
        val overlayAnimationSet = AnimatorSet()
        overlayAnimationSet.playTogether(overlayAnimator)

        stackView.swipe(SwipeDirection.Left, cardAnimationSet, overlayAnimationSet)
    }

    var handler = Handler()

    override fun onWrong(translate: Translate) {
        Log.d("Assay", "onWrong called")

        displayCorrectAnswer(translate.translate)

        swipeLeft()
        wrong++
    }

    private val dismissCorrectLayout = Runnable { layoutCorrectAnswerView.visibility = View.INVISIBLE }

    private fun displayCorrectAnswer(correctAnswer: String) {
        handler.removeCallbacks(dismissCorrectLayout)

        textCorrectAnswer.text = correctAnswer
        layoutCorrectAnswerView.visibility = View.VISIBLE
        handler.postDelayed(dismissCorrectLayout, 5000)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> finishActivity()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun finishActivity(): Boolean {
        finish()
        return true
    }


    private lateinit var words: ArrayList<Translate>

    //private var limit = PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_learn_goal",5)

    private var correct = 0
        set(value) {
            field = value
            textCorrectCount.text = "$value"
            updateProgress()
        }

    private var size = 0


    private fun updateProgress() {
        if (size == 0) return
        val answered = correct + wrong
        val progress = answered.toFloat() / size.toFloat() * 1000
        progressBar.progress = progress
        progressBar.animate()

        progressBar.text = "${size - answered}"
    }

    private var wrong = 0
        set(value) {
            field = value
            textWrongCount.text = "$value"
            updateProgress()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_assay)

        coordinator.setBackgroundColor(getApplicationColor())

        title = getString(R.string.title_activity_assay)

        dismissNotification()

        textWrongCount.text = "0"
        textCorrectCount.text = "0"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        LoadWordTask(getAppDatabase(), null) { list -> onListLoaded(list) }.execute()
    }

    private fun onListLoaded(list: ArrayList<Translate>) {
        onLoadFinished()

        words = list


        if (words.size < 4) run {
            Toast.makeText(this, R.string.error_create_quiz, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        var limit = getSharedPreferences(APP, Context.MODE_PRIVATE).getInt(QUIZ_LIMIT, 10)

        val wordToAsk = words.filter { it.correctCount < limit }
        size = wordToAsk.size

        val finalWords = wordToAsk.toMutableList()

        finalWords.shuffle()

        progressBar.text = "${finalWords.size}"
        layoutCorrectAnswerView.visibility = View.INVISIBLE

        stackView.setAdapter(QuizAdapter(this, finalWords, words, this))

        finishQuiz.setOnClickListener { finishActivity() }
    }

    private fun onLoadFinished() {
        layoutMain.visibility = View.VISIBLE
        textNoMoreQuestion.visibility = View.VISIBLE
        loading.visibility = View.GONE
    }

    private fun dismissNotification() {
        val i = Intent(this, NotifictionaryService::class.java)
        i.action = ACTION_DISMISS_QUIZ_NOTIFICATION
        startService(i)
    }

    override fun onBackPressed() {

    }

}