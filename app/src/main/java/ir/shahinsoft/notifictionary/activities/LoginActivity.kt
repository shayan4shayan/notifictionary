package ir.shahinsoft.notifictionary.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import ir.shahinsoft.notifictionary.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login.setOnClickListener { checkAndLogin() }
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
            //TODO send login request to server

            //TODO remove code below
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun isDataValid(): Boolean {
        val name = textFirstName.text.toString()
        val last = textLastName.text.toString()
        var isValid = true
        if (name.isEmpty()) {
            layoutFirstName.error = getString(R.string.error_empty)
            isValid = false
        }
        if (last.isEmpty()) {
            layoutLastName.error = getString(R.string.error_empty)
            isValid = false
        }
        return isValid
    }
}
