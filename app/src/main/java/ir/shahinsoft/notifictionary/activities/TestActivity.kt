package ir.shahinsoft.notifictionary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.utils.NotificationUtil

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val translate = Translate().apply {
            name = "harmonica"
            translate = "ساز دهنی"
            lang = "en-fa"
            id = 10
        }

        NotificationUtil.sendNotification(this, translate)

        val t2 = Translate().apply {
            name = "compromise"
            this.translate = "سازش"
            lang = "en-fa"
            id = 12
        }

        NotificationUtil.sendNotification(this, t2)



    }
}
