package ir.shahinsoft.notifictionary.activities

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        telegram.setOnClickListener { openTelegramPage() }
        instagram.setOnClickListener { openInstagramPage() }
        twitter.setOnClickListener { openTwitterPage() }
        gmail.setOnClickListener { sendGmail() }
        close.setOnClickListener { finish() }
    }

    private fun sendGmail() {
        val i = Intent(ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(EXTRA_EMAIL, arrayOf("shayan4shayan@gmail.com"))
        startActivity(createChooser(i, "Send Email to ..."))
    }

    private fun openTwitterPage() {
        Intent(ACTION_VIEW).apply {
            data = Uri.parse("https://twitter.com/shayand4")
            startActivity(this)
        }
    }

    private fun openInstagramPage() {
        Intent(ACTION_VIEW).apply {
            data = Uri.parse("https://www.instagram.com/shayandarvishpour/")
            startActivity(this)
        }
    }

    private fun openTelegramPage() {
        Intent(ACTION_VIEW).apply {
            data = Uri.parse("https://t.me/shayandarvishpour")
            startActivity(this)
        }
    }
}
