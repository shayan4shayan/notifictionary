package ir.shahinsoft.notifictionary.prefrences

import android.content.Context
import android.preference.DialogPreference
import android.preference.EditTextPreference
import android.preference.Preference
import android.util.AttributeSet
import ir.shahinsoft.notifictionary.widget.ColorPickerDialog

class ThemePrefrence(context: Context, attributeSet: AttributeSet?, defstyle: Int) : EditTextPreference(context, attributeSet, defstyle) {
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    override fun onClick() {
        val dialog = ColorPickerDialog(context) { color -> onColorChanged(color) }
        dialog.show()
    }

    private fun onColorChanged(color: Int) {
        persistInt(color)
        notifyDependencyChange(shouldDisableDependents())
        notifyChanged()
    }
}