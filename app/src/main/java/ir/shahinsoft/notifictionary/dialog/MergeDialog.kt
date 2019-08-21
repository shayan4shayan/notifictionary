package ir.shahinsoft.notifictionary.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.tasks.LoadCategoryTask
import ir.shahinsoft.notifictionary.tasks.MergeTask
import ir.shahinsoft.notifictionary.toast
import kotlinx.android.synthetic.main.dialog_merge.*
import java.util.ArrayList

class MergeDialog(context: Context, private val source:Category) : AlertDialog(context){


    override fun show() {
        super.show()
        setContentView(R.layout.dialog_merge)
        val txt = String.format(title.text.toString(),source.name)
        title.text = txt
        merge.setOnClickListener {
            doMerge()
        }
        merge.isEnabled = false
        LoadCategoryTask(context.getAppDatabase()) {
            displayCategories(it)
        }.execute()
    }

    private fun displayCategories(it: ArrayList<Category>) {
        it.remove(it.find { it.id == source.id })
        recycler.adapter = Adapter(it)
        merge.isEnabled = true
    }

    private fun doMerge() {
        val target = (recycler.adapter as Adapter).selectedCategory()
        if (target == null) {
            context.toast(R.string.error_category_not_selected)
            return
        }
        MergeTask(context.getAppDatabase(),source,target,delete_category.isChecked) {
            context.toast(R.string.merge_success)
            dismiss()
        }.execute()

    }

    class Adapter(private val items : ArrayList<Category>) : RecyclerView.Adapter<ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val holder = ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_category_selectable,parent,false)
            )
            holder.itemView.setOnClickListener {
                items.forEach {it.isSelected = false}
                items[holder.adapterPosition].isSelected = true
                notifyDataSetChanged()
            }
            holder.mSelectView.setOnClickListener {
                items.forEach {it.isSelected = false}
                items[holder.adapterPosition].isSelected = true
                notifyDataSetChanged()
            }
            holder.mNameView.setOnClickListener {
                items.forEach {it.isSelected = false}
                items[holder.adapterPosition].isSelected = true
                notifyDataSetChanged()
            }
            return holder
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val category = items[position]
            holder.mNameView.text = category.name
            holder.mSelectView.isChecked = category.isSelected
        }

        fun selectedCategory(): Category? {
            return items.find { it.isSelected }
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val mNameView: TextView = itemView.findViewById(R.id.title)

        val mSelectView: RadioButton = itemView.findViewById(R.id.select)
    }
}