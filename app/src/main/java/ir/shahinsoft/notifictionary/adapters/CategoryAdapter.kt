package ir.shahinsoft.notifictionary.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Category

/**
 * Created by shayan4shayan on 4/6/18.
 */
class CategoryAdapter(val cats: ArrayList<Category>, private val listener: OnRemoveListener) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        )
        holder.image.setOnClickListener { listener.onRemove(cats[holder.adapterPosition]) }
        holder.color.setOnClickListener { listener.onSelectColor(cats[holder.adapterPosition]) }
        holder.itemView.setOnClickListener {
            clearSelection()
            cats[holder.adapterPosition].isSelected = true
            listener.onCategorySelected(cats[holder.adapterPosition])
            notifyDataSetChanged()
        }

        return holder
    }

    private fun clearSelection() {
        cats.forEach { it.isSelected = false }
    }

    override fun getItemCount() = cats.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = cats[position].name
        holder.color.setCardBackgroundColor(cats[position].color)
        if (cats[position].isSelected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
        if (cats[position].id < 0) {
            holder.color.visibility = View.GONE
            holder.image.visibility = View.GONE
        } else {
            holder.color.visibility = View.VISIBLE
            holder.image.visibility = View.VISIBLE
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image = itemView.findViewById(R.id.image) as ImageView

        val text = itemView.findViewById(R.id.text) as TextView

        val color = itemView.findViewById<CardView>(R.id.cardColor)

    }

    interface OnRemoveListener {
        fun onRemove(cat: Category)
        fun onSelectColor(cat: Category)
        fun onCategorySelected(category: Category)
    }
}