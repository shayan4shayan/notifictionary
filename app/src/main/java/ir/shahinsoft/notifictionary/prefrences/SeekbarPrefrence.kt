package ir.shahinsoft.notifictionary.prefrences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceViewHolder
import ir.shahinsoft.notifictionary.R

class SeekbarPrefrence(context: Context, attributes: AttributeSet?, defstyle: Int) : EditTextPreference(context, attributes, defstyle) {

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)
    constructor(context: Context) : this(context, null)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val view = holder?.itemView
        val seekbar = view?.findViewById<SeekBar>(R.id.speed_seek)
        val text = view?.findViewById<TextView>(R.id.textSpeed)
        if (getText()==null || getText().isEmpty()) setText("50")
        text?.text = getText()
        seekbar?.progress = getText().toInt()
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                text?.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                setText("${seekBar?.progress}")
            }

        })
    }
}