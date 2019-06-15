package ir.shahinsoft.notifictionary

import android.content.Context
import android.content.SharedPreferences
import ir.shahinsoft.notifictionary.widget.MessageDialog
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.experimental.and
import kotlin.experimental.or

const val SHARED_PREFERENCE_NAME = "user"
const val USERNAME = "username"
const val EMAIL = "email"
const val POINTS = "points"
const val DEFAULT_STRING = "Guest User"
const val LOGIN_STATE = "login_state"


fun getSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

fun saveUsername(context: Context, username: String) = getSharedPreferences(context).edit().putString(USERNAME, username).apply()

fun saveEmail(context: Context, email: String) = getSharedPreferences(context).edit().putString(EMAIL, email).apply()

fun savePoints(context: Context, point: Int) = getSharedPreferences(context).edit().putInt(POINTS, point).apply()

fun getUsername(context: Context) = getSharedPreferences(context).getString(USERNAME, DEFAULT_STRING)

fun getEmail(context: Context) = getSharedPreferences(context).getString(EMAIL, "")

fun getPoints(context: Context) = getSharedPreferences(context).getInt(POINTS, 0)

fun getEmailMd5(context: Context): String {
    val digest = MessageDigest.getInstance("MD5")
    var email = getEmail(context)!!.toByteArray(Charset.defaultCharset())
    if (email.isEmpty()) return ""
    digest.update(email)
    email = digest.digest()
    val sb = StringBuffer()
    for (i in 0 until email.size) {
        var h = Integer.toHexString(0xFF and email[i].toInt())
        while (h.length < 2)
            h = "0$h"
        sb.append(h)
    }
    return sb.toString()
}

fun getGravatarUrl(context: Context) = "https://www.gravatar.com/avatar/" + getEmailMd5(context)

fun isLogin(context: Context) = getSharedPreferences(context).getBoolean(LOGIN_STATE, false)

fun setLogin(context: Context, boolean: Boolean) {
    getSharedPreferences(context).edit().putBoolean(LOGIN_STATE, boolean).apply()
    if (!boolean) {
        getSharedPreferences(context).edit().clear().apply()
    }
}