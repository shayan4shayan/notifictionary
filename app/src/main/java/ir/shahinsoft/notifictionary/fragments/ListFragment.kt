package ir.shahinsoft.notifictionary.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.ExportAdapter
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.LoadWordTask
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment(), ExportAdapter.OnWordSelectListener {
    override fun onMove(word: Translate, category: Category) {
        listener.moveWord(this, word, this.category, category)
        recycler.adapter?.notifyDataSetChanged()
    }

    override fun getCategooryList(): List<Category> {
        return listener.getCategories()
    }

    override fun onSelected(word: Translate) {
        listener.onWordSelected(category, word)
    }

    lateinit var listener: OnWordSelected
    public lateinit var category: Category

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler.itemAnimator = DefaultItemAnimator()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        onListLoaded(listener.getWords(category))
    }

    private fun onListLoaded(words: ArrayList<Translate>) {
        progress.visibility = View.GONE
        recycler.adapter = ExportAdapter(words, this)
    }

    fun updateList() {
        if (recycler == null || recycler.adapter == null) return
        recycler.adapter?.notifyDataSetChanged()
    }

    interface OnWordSelected {
        fun onWordSelected(category: Category, word: Translate)
        fun moveWord(fragment: ListFragment, word: Translate, category: Category, target: Category)
        fun getCategories(): List<Category>
        fun getWords(category: Category): ArrayList<Translate>
    }
}