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
        val color = getApplicationColor()
        changeTheme(color)
        updateTitle()
    }

    private fun updateTitle() {
        val title = title.toString()
        val font = ResourcesCompat.getFont(this, R.font.permanent_marker_regular)
        val spannable = SpannableString(title)
        spannable.setSpan(CustomTypefaceSpan("", font!!), 0, title.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        setTitle(spannable)
    }

    fun getApplicationColor() = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_theme", ContextCompat.getColor(this, R.color.picker_red).toString())!!.toInt()

    open fun changeTheme(color: Int) {
        val dark = manipulateColor(color, 0.8f)
        setActionBarColor(color)
        setStatusBarColor(dark)
    }

    fun manipulateColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor)
        val g = Math.round(Color.green(color) * factor)
        val b = Math.round(Color.blue(color) * factor)
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255))
    }

    private fun setActionBarColor(@ColorInt color: Int) {
        if (actionBar != null)
            actionBar!!.setBackgroundDrawable(ColorDrawable(color))
        if (supportActionBar != null) {
            supportActionBar!!.setBackgroundDrawable(ColorDrawable(color))
        }
    }

    private fun setStatusBarColor(@ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val window = window

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            // finally change the color
            window.statusBarColor = color
        }
    }
}