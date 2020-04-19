package ir.shahinsoft.notifictionary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.Board

/**
 * Created by shayan4shayan on 4/6/18.
 */
class CategoryAdapter(val boards: ArrayList<Board>, val onBoardSelected: (Board) -> Unit) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    val viewTypePurple = 0
    val viewTypeOrange = 1


    override fun getItemViewType(position: Int): Int {
        return ((position + 1) / 2) % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                if (viewType == viewTypePurple) {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_category_primary, parent, false)
                } else {
                    LayoutInflater.from(parent.context).inflate(R.layout.item_category_secondary, parent, false)
                }
        ).apply {
            itemView.setOnClickListener {
                onBoardSelected(boards[adapterPosition])
            }
        }
    }

    override fun getItemCount() = boards.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board = boards[position]
        holder.title.text = board.name
        holder.leanredCount.text = "${board.learnedCount}"
        holder.totalCount.text = "${board.totalCount}"
        var progress = board.learnedCount.toFloat() / board.totalCount.toFloat()
        if (board.totalCount ==0){
            progress = 0f
        }
        if (progress < 0.33) {
            holder.image.setImageResource(R.drawable.ic_boards_learning)
        } else if (progress >= 0.33 && progress < 0.66) {
            holder.image.setImageResource(R.drawable.ic_boards_learned_half)
        } else {
            holder.image.setImageResource(R.drawable.ic_board_learned)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image = itemView.findViewById(R.id.image) as ImageView

        val title = itemView.findViewById(R.id.title) as TextView

        val leanredCount = itemView.findViewById<TextView>(R.id.learnedCount)

        val totalCount = itemView.findViewById<TextView>(R.id.totalCount)

    }
}