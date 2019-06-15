package ir.shahinsoft.notifictionary.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.activity_resault.*

class ResultActivity : BaseActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resault)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.title_result)

        if (getAll() == 0) textPercent.text = "0%"
        else
            textPercent.text = String.format("%1.1f", getPercent()) + "%"

        textCorrect.text = "${getString(R.string.correct)} : ${getCorrect()}"
        textWrong.text = "${getString(R.string.wrong)} : ${getWrong()}"
        textAll.text = "${getString(R.string.all)} : ${getAll()}"
        close.setOnClickListener { finish() }
    }

    private fun getPercent() = getCorrect().toFloat() / getAll().toFloat() *100

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun getCorrect() = intent.getIntExtra("correct", 0)
    fun getWrong() = intent.getIntExtra("wrong", 0)
    fun getAll() = getCorrect() + getWrong()
}
