package ir.shahinsoft.notifictionary.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceFragment
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.toast
import ir.shahinsoft.notifictionary.widget.ColorPickerDialog

class SettingActivityPhone : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_setting)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.action_settings)
        fragmentManager
                .beginTransaction()
                .replace(R.id.layout, AllSettingsFragment())
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class AllSettingsFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_settings_phone)

            initTheme()

            initAndroidLowerThan8()
        }

        private fun initAndroidLowerThan8() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                findPreference("pref_notification_sound").apply {
                    isEnabled = false
                    shouldDisableView = true
                }
                findPreference("pref_notification_vibration").apply {
                    isEnabled = false
                    shouldDisableView = true
                }
                findPreference("android_settings").apply {
                    isEnabled = true
                    shouldDisableView = false
                    setOnPreferenceClickListener {
                        val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        startActivity(settingsIntent)
                        true
                    }
                }
            }
        }

        private fun initTheme() {
            val pref = findPreference("pref_theme") as EditTextPreference

            pref.setOnPreferenceClickListener {
                pref.dialog.dismiss()
                ColorPickerDialog(activity!!) { color -> onColorChanged(pref, color) }.show()
                true
            }

            pref.setOnPreferenceChangeListener { _, newValue ->
                val color = (newValue as String).toInt()
                activity?.toast("$color")
                (activity as SettingsActivity).changeTheme(color)
                true
            }
        }

        private fun onColorChanged(pref: EditTextPreference, color: Int) {
            pref.text = "$color"
            (activity as BaseActivity).changeTheme(color)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                activity?.finish()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }
}