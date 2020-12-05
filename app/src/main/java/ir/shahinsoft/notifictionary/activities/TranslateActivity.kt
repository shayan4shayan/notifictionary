package ir.shahinsoft.notifictionary.activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.EXTRA_PROCESS_TEXT
import android.content.Intent.EXTRA_PROCESS_TEXT_READONLY
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import ir.shahinsoft.notifictionary.dialog.TranslateBottomSheet

class TranslateActivity : BaseActivity() {
    lateinit var text: String
    private var isReadonly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text = intent.getStringExtra(EXTRA_PROCESS_TEXT) ?: ""
            isReadonly = intent.getBooleanExtra(EXTRA_PROCESS_TEXT_READONLY, true)
            TranslateBottomSheet(this, isReadonly, text) {
                if (!isReadonly && it.isNotEmpty()) {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(EXTRA_PROCESS_TEXT, it)
                    })
                }
                finish()
            }.show()
        } else {
            finish()
        }
    }
}