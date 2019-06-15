package ir.shahinsoft.notifictionary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.History

class HistoryAdapter(private val items: ArrayList<History>, val listener: OnItemActionClickListener) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 1) {
            return ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
            )
        } else {
            val holder = ExtendedViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_history_extended, parent, false)
            )
            holder.add.setOnClickListener {
                listener.onInsert(items[0].apply { word = holder.word.text.toString(); translate = holder.translate.text.toString() })
                listener.onDelete(items[0])
            }

            holder.remove.setOnClickListener {
                listener.onDelete(items[0])
            }
            return holder
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = items[position]
        holder.word.text = t.word
        holder.translate.text = t.translate
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }


    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val word = view.findViewById<TextView>(R.id.textWord)
        val translate = view.findViewById<TextView>(R.id.textTranslate)
    }

    class ExtendedViewHolder(view: View) : ViewHolder(view) {
        val remove = view.findViewById<View>(R.id.remove)
        val add = view.findViewById<View>(R.id.add)
    }

    interface OnItemActionClickListener {
        fun onDelete(t: History)
        fun onInsert(t: History)
    }
}