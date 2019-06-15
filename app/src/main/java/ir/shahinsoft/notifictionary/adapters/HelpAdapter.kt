package ir.shahinsoft.notifictionary.adapters

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.languagenotifier.model.Help
import ir.shahinsoft.notifictionary.R

class HelpAdapter(val list: List<Help>) : RecyclerView.Adapter<HelpAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_help, parent, false
        ))
        holder.image.setOnClickListener { changeItemState(holder) }
        holder.image.animation = AnimationUtils.loadAnimation(parent.context, R.anim.rotate)
        return holder
    }

    private fun changeItemState(holder: ViewHolder) {
        val isSelected = list[holder.adapterPosition].isSelected
        if (isSelected) {
            list[holder.adapterPosition].isSelected = false
            holder.content.visibility = View.GONE
            animate2(holder.image)
        } else {
            list[holder.adapterPosition].isSelected = true
            holder.content.visibility = View.VISIBLE
            animate(holder.image)
        }
    }

    private fun animate2(image: ImageView?) {
        val animator = ObjectAnimator.ofFloat(image,"rotation",180f,0f)
        animator.duration = 200
        animator.start()
    }

    private fun animate(image: ImageView?) {
        val animator = ObjectAnimator.ofFloat(image,"rotation",0f,180f)
        animator.duration = 200
        animator.start()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(list[position].title)
        holder.content.setText(list[position].content)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = itemView.findViewById<TextView>(R.id.textTitle)
        val content = itemView.findViewById<TextView>(R.id.textContent)
        val image = itemView.findViewById<ImageView>(R.id.image)
    }

}