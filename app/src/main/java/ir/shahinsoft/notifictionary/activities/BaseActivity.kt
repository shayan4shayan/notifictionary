package ir.shahinsoft.notifictionary.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.preference.PreferenceManager
import android.text.SpannableString
import android.text.style.TypefaceSpan
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import ir.shahinsoft.notifictionary.APP
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.THEME
import ir.shahinsoft.notifictionary.utils.CustomTypefaceSpan


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

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
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
    }
}