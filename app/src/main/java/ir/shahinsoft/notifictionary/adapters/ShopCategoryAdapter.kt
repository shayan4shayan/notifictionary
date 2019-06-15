package ir.shahinsoft.notifictionary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.model.ShopCategory
import ir.shahinsoft.notifictionary.widget.AppTextView

class ShopCategoryAdapter(val onCategoryClicked: (ShopCategory) -> Unit) : RecyclerView.Adapter<ShopCategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image_category)!!
        val text = view.findViewById<AppTextView>(R.id.text_category)!!
    }

    var items = ArrayList<ShopCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopCategoryAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_shop_category, parent, false)).apply {
            itemView.setOnClickListener { onCategoryClicked(items[adapterPosition]) }
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ShopCategoryAdapter.ViewHolder, position: Int) {
        val currItem = items[position]
        Picasso.get().load(currItem.toUrl()).into(holder.image)
        holder.text.text = currItem.toString()
    }

}