package ir.shahinsoft.notifictionary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import java.lang.Appendable

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toast(mills: Long) {
    var mil = mills
    var factor = 1000 * 60 * 60
    val hour = (mil / factor)

    mil %= factor // at least 1000 * 60 * 60
    factor /= 60 // 1000*60
    val min = (mil / factor)

    mil %= factor //at least 1000 * 60
    factor /= 60 //1000
    val sec = (mil / factor)

    Handler().postDelayed({
        toast("notification will be sent in ${if (hour > 0) "$hour hour(s) " else ""}${if (min > 0) "$min min(s) " else ""}$sec seconds")
    }, 5000)
}

fun Context.translateToast(message: String) {
    val toast = Toast.makeText(this, "", Toast.LENGTH_LONG)
    toast.view = LayoutInflater.from(this).inflate(R.layout.custom_toast, null, false)
    val tv = toast.view?.findViewById<TextView>(R.id.text)
    tv?.text = message
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

fun Context.getColorCompat(@ColorRes int: Int): Int {
    return ContextCompat.getColor(this, int)
}

fun Context.toast(@StringRes res: Int) {
    Toast.makeText(this, res, Toast.LENGTH_LONG).show()
}

fun Context.startActivity(c: Class<out Activity>) {
    startActivity(Intent(this, c))
}

fun Context.startActivity(c: Class<out Activity>, bundle: Bundle) {
    Intent(this, c).apply {
        putExtras(bundle)
        startActivity(this)
    }
}

fun Context.startActivity(c: Class<out Activity>, action: Uri) {
    Intent(this, c).apply {
        this.data = action
        startActivity(this)
    }
}

fun Context.getAppDatabase(): DatabaseWrapper {
    return DatabaseWrapper.getInstance(this)
}

fun Context.isFirstLaunch(): Boolean {
    val b = getSharedPreferences(APP, Context.MODE_PRIVATE).getBoolean(FIRST_LAUNCH, true)
    getSharedPreferences(APP, Context.MODE_PRIVATE).edit().putBoolean(FIRST_LAUNCH, false).apply()
    return b
}

fun Context.firstLaunchFinished() {
    getSharedPreferences(APP, Context.MODE_PRIVATE).edit().putBoolean(FIRST_LAUNCH, false).apply()
}

fun Context.isLicenseAccepted(): Boolean {
    val tmp = getSharedPreferences(APP, Context.MODE_PRIVATE).getBoolean(LICENSE_ACCEPTANCE, false)
    return tmp
}