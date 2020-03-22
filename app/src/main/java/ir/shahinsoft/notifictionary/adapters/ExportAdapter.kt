package ir.shahinsoft.notifictionary.adapters

import android.view.LayoutInflater
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate

/**
 * Created by shayan4shayan on 3/11/18.
 */
class ExportAdapter(val list: ArrayList<Translate>, private val callback: OnWordSelectListener?) : RecyclerView.Adapter<ExportAdapter.ViewHolder>() {
    constructor(list: ArrayList<Translate>) : this(list, null)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.word.text = item.name
        holder.translate.text = item.translate
//        holder.hint.text = item.hint
        if (item.selected)
            holder.check.visibility = View.VISIBLE
        else
            holder.check.visibility = View.INVISIBLE
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_selectable_word, parent, false))
        holder.itemView.setOnClickListener { onItemClicked(holder.adapterPosition) }
        holder.check.setOnClickListener { onItemClicked(holder.adapterPosition) }
        holder.more.setOnClickListener { displayOptionsMenu(holder) }
        return holder
    }

    private fun displayOptionsMenu(holder: ViewHolder) {
        val word = list[holder.adapterPosition]
        val menu = PopupMenu(holder.itemView.context, holder.more)
        val select = menu.menu.add(if (word.selected) R.string.unselect else R.string.select)
        val move = menu.menu.addSubMenu(R.string.move)

        val categories = callback?.getCategooryList()
        categories?.forEach { addSubmenu(it, move, word) }
        select.setOnMenuItemClickListener { onItemClicked(holder.adapterPosition);true }
        menu.show()
    }

    private fun addSubmenu(board: Board, subMenu: SubMenu?, word: Translate) {
        val sub = subMenu?.add(board.name)
        sub?.setOnMenuItemClickListener { callback?.onMove(word, board); true }
    }

    private fun onItemClicked(adapterPosition: Int) {
        list[adapterPosition].selected = !list[adapterPosition].selected
        callback?.onSelected(list[adapterPosition])
        //notifyDataSetChanged()
        notifyItemChanged(adapterPosition)
    }

    fun getSelected() = list.filter { it.selected }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val word = view.findViewById<TextView>(R.id.text_word)!!
        val translate = view.findViewById<TextView>(R.id.text_translate)!!
        //        val hint = view.findViewById<TextView>(R.id.text_hint)!!
        val check = view.findViewById<ImageView>(R.id.check)!!
        val more = view.findViewById<ImageView>(R.id.more)
    }

    fun selectAll() {
        list.forEach { it.selected = true }
        notifyItemRangeChanged(0, list.size)
    }

    fun diselectAll() {
        list.forEach { it.selected = false }
        notifyItemRangeChanged(0, list.size)
    }

    interface OnWordSelectListener {
        fun onSelected(word: Translate)
        fun onMove(word: Translate, board: Board)
        fun getCategooryList(): List<Board>
    }
}