package ir.shahinsoft.notifictionary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.TranslateLanguage
import java.util.*

class SelectLanguageAdapter(val list: List<TranslateLanguage>, val listener: (TranslateLanguage) -> Unit)
    : RecyclerView.Adapter<SelectLanguageAdapter.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(getResourceId(viewType), parent, false)).apply {
            name.setOnClickListener {
                listener(list[adapterPosition])
            }
        }
    }

    private fun getResourceId(viewType: Int): Int {
        return if (viewType == 1) {
            R.layout.item_language_primary
        } else {
            R.layout.item_language_secondary
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list[position].toString().toUpperCase(Locale.US)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.text)

    }
}