package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.view.MenuItem
import ir.shahinsoft.notifictionary.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setTitle(R.string.login)
        login.setOnClickListener { checkAndLogin() }
        textEmail.setOnEditorActionListener { _, _, _ -> checkAndLogin(); true }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkAndLogin() {
        if (isDataValid()) {
            val name = textFirstName.text.toString()
            val last = textLastName.text.toString()
            val email = textEmail.text.toString()
            saveUsername(this, "$name $last")
            saveEmail(this, email)
            savePoints(this, getPoints(this))
            setLogin(this, true)
            finish()
            toast(R.string.login_success)
        }
    }

    private fun isDataValid(): Boolean {
        val name = textFirstName.text.toString()
        val last = textLastName.text.toString()
        val email = textEmail.text.toString()
        var isValid = true
        if (name.isEmpty()) {
            layoutFirstName.error = getString(R.string.error_empty)
            isValid = false
        }
        if (last.isEmpty()) {
            layoutLastName.error = getString(R.string.error_empty)
            isValid = false
        }
        if (email.isEmpty()) {
            layoutEmail.error = getString(R.string.error_empty)
            return false
        }
        if (!email.contains("@")) {
            layoutEmail.error = getString(R.string.error_email)
            return false
        }
        return isValid
    }
}
