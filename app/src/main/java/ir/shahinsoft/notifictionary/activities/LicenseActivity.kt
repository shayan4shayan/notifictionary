package ir.shahinsoft.notifictionary.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import ir.shahinsoft.languagenotifier.model.License
import ir.shahinsoft.notifictionary.R
import kotlinx.android.synthetic.main.activity_license.*

class LicenseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        title = getString(R.string.title_license)
        val arr = ArrayList<License>()
        init(arr)
        recycler.adapter = ArrayAdapter<License>(this, R.layout.simple_text, arr)
        recycler.setOnItemClickListener { _, _, pos, _ -> onItemClicked(arr[pos]) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onItemClicked(license: License) {
        if (license.isUrl) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(license.url)
            startActivity(i)
        } else if (license.isEmail) {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf(license.url))
            startActivity(Intent.createChooser(i,"Send Email to ..."))
        }
    }

    private fun init(arr: ArrayList<License>) {
        arr.add(License("Card stack view", "https://github.com/yuyakaido/CardStackView"))
        arr.add(License("Circle picker", "https://github.com/bugadani/CirclePicker"))
        arr.add(License("Google", "http://developer.android.com"))
        arr.add(License("Volley", "https://github.com/google/volley"))
        arr.add(License("Sticky nested scroll view", "https://github.com/didikk/sticky-nestedscrollview"))
        arr.add(License("Material intro screen", "https://github.com/TangoAgency/material-intro-screen"))
        arr.add(License("Crashlytics", "https://github.com/crashlytics"))
        arr.add(License("Mitra Lashkary Designer of Application", "mitra.l76@gmail.com"))
    }
}
