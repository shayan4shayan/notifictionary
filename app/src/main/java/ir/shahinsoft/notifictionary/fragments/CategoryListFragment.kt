package ir.shahinsoft.notifictionary.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.shahinsoft.notifictionary.*
import ir.shahinsoft.notifictionary.adapters.WordsAdapter
import ir.shahinsoft.notifictionary.dialog.DeleteBottomSheet
import ir.shahinsoft.notifictionary.dialog.EditBottomSheet
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.services.NotifictionaryService
import ir.shahinsoft.notifictionary.tasks.*
import kotlinx.android.synthetic.main.fragment_category_list.*

class CategoryListFragment : androidx.fragment.app.Fragment() {

    var catId = -1
    var page = 0
    lateinit var adapter: WordsAdapter

    private val onWordActionListener = object : WordsAdapter.OnItemActionListener {
        override fun onDeleted(translate: Translate?) {
            if (translate != null)
                DeleteBottomSheet(context!!, translate) {
                    DeleteTranslateTask(context?.getAppDatabase()!!).execute(translate)
                    val index = adapter.translates.indexOf(translate)
                    val isDeleted = adapter.translates.remove(translate)
                    if (isDeleted) {
                        adapter.notifyItemRemoved(index)
                    }
                    checkForNotFoundLayout()
                }.show()
        }

        override fun onEdit(translate: Translate?) {
            if (translate != null)
                EditBottomSheet(context!!, translate) {
                    UpdateTask(context!!.getAppDatabase()).execute(translate)
                    adapter.notifyItemChanged(adapter.translates.indexOf(it))
                }.show()
        }

        override fun play(name: String?) {
            Intent(context, NotifictionaryService::class.java).apply {
                action = ACTION_SPEAK
                putExtra(EXTRA_WORD, name)
                context?.startService(this)
            }
        }

        override fun onMove(translate: Translate?, category: Category?) {
            if (translate != null && category != null) {
                translate.catId = category.id
                UpdateTask(context?.getAppDatabase()!!).execute(translate)
                //remove from current category if id was changed
                if (catId >= 0 && category.id != catId) {
                    val index = adapter.translates.indexOf(translate)
                    adapter.translates.remove(translate)
                    adapter.notifyItemRemoved(index)
                    onWordMoveListener.onMove(translate, category.id)
                }
                checkForNotFoundLayout()
            }
        }

        override fun canShowMoveMenu(): Boolean {
            return catId >= 0
        }

    }

    var isLoading = false

    lateinit var onWordMoveListener: OnWordMoveListener

    private val scrollListener = object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
            val size = adapter.itemCount
            val pos = layoutManager.findLastVisibleItemPosition()
            if (size - pos <= 50 && !isLoading) {
                isLoading = true
                page++
                loadData()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = WordsAdapter(ArrayList<Category>(), onWordActionListener)
        recycler.adapter = adapter
        recycler.addOnScrollListener(scrollListener)
        recycler.isNestedScrollingEnabled = true
        recycler.setItemViewCacheSize(20)
        Log.d("CategoryListFragment", "OnViewCreated called: $catId")
        //onSelected()
    }

    override fun setArguments(args: Bundle?) {
        if (args != null)
            catId = args.getInt(CATEGORY_ID)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.i("CategoryListFragment", "onAttach called : $catId")
    }

    private fun onSelected() {
        Log.d("CategoryListFragment", "translates size: ${adapter.translates.size}")
        loadData()
        updateCategory()
    }

    private fun updateCategory() {
        LoadCategoryTask(context!!.getAppDatabase()) {
            adapter.setCategories(it)
        }.execute()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("CategoryListFragment", "onDetach called: $catId")
    }

    override fun onResume() {
        super.onResume()
        page = 0
        onSelected()
        Log.d("CategoryListFragment", "onResume $catId")
    }

    override fun onPause() {
        super.onPause()
        adapter.translates.clear()
        page = 0
        isLoading = false
        adapter.notifyDataSetChanged()
        Log.d("CategoryListFragment", "onPause $catId")
    }

    private fun loadData() {
        isLoading = true
        LoadWordTask(context!!.getAppDatabase(), if (catId >= 0) catId else null) { onAddToList(it) }.execute(page)
    }

    private fun onAddToList(it: ArrayList<Translate>) {
        adapter.add(it)
        isLoading = false
        checkForNotFoundLayout()
    }

    private fun checkForNotFoundLayout() {
        if (adapter.translates.size == 0) {
            recycler.visibility = View.GONE
            layoutNotFound.visibility = View.VISIBLE
        } else {
            recycler.visibility = View.VISIBLE
            layoutNotFound.visibility = View.GONE
        }
    }

    fun onNewTranslate(translate: Translate) {
        adapter.translates.add(0, translate)
        adapter.notifyItemInserted(0)

        checkForNotFoundLayout()
    }

    fun onWordInserted(translate: Translate) {
        adapter.translates.add(0, translate)
        adapter.notifyItemInserted(0)

        checkForNotFoundLayout()
    }

    interface OnWordMoveListener {
        fun onMove(translate: Translate, catId: Int)
    }
}