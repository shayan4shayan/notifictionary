package ir.shahinsoft.notifictionary.activities

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        telegram.setOnClickListener { openTelegramPage() }
        instagram.setOnClickListener { openInstagramPage() }
        twitter.setOnClickListener { openTwitterPage() }
        gmail.setOnClickListener { sendGmail() }
        close.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        setStatusBarColor()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val window = window

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            // finally change the color
            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary_text_color)
        }
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
