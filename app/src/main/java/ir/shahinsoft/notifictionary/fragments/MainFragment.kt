package ir.shahinsoft.notifictionary.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.databinding.FragmentMainBinding
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.LoadRecentTask

class MainFragment : androidx.fragment.app.Fragment() {

    lateinit var binding : FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadRecent()
        Log.d("MainFragment","onViewCreated")
    }

    private fun loadRecent() {
        context?.apply {
            LoadRecentTask(getAppDatabase()){
                displayRecent(it)
            }.execute()
        }
    }

    private fun displayRecent(recent:List<Translate>){
        binding.recentRecycler.adapter = RecentAdapter(recent)
        Log.d("MainFragment","displaying recent")
    }

}

class RecentAdapter(val recent:List<Translate>) : RecyclerView.Adapter<RecentViewHolder>(){

    override fun getItemViewType(position: Int) = ((position + 1) / 2) % 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val view = if (viewType == 0){
            LayoutInflater.from(parent.context).inflate(R.layout.item_recent_primary,parent,false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_recent_secondary,parent,false)
        }
        return RecentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val data = recent[position]

        holder.word.text = data.name
        holder.translate.text = data.translate
    }

    override fun getItemCount() = recent.size


}

class RecentViewHolder(view :View) : RecyclerView.ViewHolder(view){
    val word = view.findViewById<TextView>(R.id.textWord)
    val translate = view.findViewById<TextView>(R.id.textTranslate)
}