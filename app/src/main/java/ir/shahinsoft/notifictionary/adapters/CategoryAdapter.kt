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
import ir.shahinsoft.notifictionary.model.Board

/**
 * Created by shayan4shayan on 4/6/18.
 */
class CategoryAdapter(val boards: ArrayList<Board>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        )


        return holder
    }

    private fun clearSelection() {
        boards.forEach { it.isSelected = false }
    }

    override fun getItemCount() = boards.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image = itemView.findViewById(R.id.image) as ImageView

        val text = itemView.findViewById(R.id.text) as TextView

    }

    interface OnRemoveListener {
        fun onRemove(cat: Board)
        fun onSelectColor(cat: Board)
        fun onCategorySelected(board: Board)
    }
}