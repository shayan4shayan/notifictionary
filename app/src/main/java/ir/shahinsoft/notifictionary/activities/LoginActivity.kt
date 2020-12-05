package ir.shahinsoft.notifictionary.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener { checkAndLogin() }
    }

    private fun checkAndLogin() {
        if (isDataValid()) {
            val name = binding.textFirstName.text.toString()
            val last = binding.textLastName.text.toString()
            //TODO send login request to server

            //TODO remove code below
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun isDataValid(): Boolean {
        val name = binding.textFirstName.text.toString()
        val last = binding.textLastName.text.toString()
        var isValid = true
        if (name.isEmpty()) {
            binding.layoutFirstName.error = getString(R.string.error_empty)
            isValid = false
        }
        if (last.isEmpty()) {
            binding.layoutLastName.error = getString(R.string.error_empty)
            isValid = false
        }
        return isValid
    }
}
