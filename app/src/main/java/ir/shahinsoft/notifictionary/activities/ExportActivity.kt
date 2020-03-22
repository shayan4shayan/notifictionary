package ir.shahinsoft.notifictionary.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.adapters.ExportPagerAdapter
import ir.shahinsoft.notifictionary.fragments.ListFragment
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.BoardsLoaderTask
import ir.shahinsoft.notifictionary.tasks.LoadCategoryTask
import ir.shahinsoft.notifictionary.utils.Exporter
import ir.shahinsoft.notifictionary.widget.GetNameDialog
import kotlinx.android.synthetic.main.activity_import.*

open class ExportActivity : BaseActivity(), ExportPagerAdapter.FragmentProvider, ListFragment.OnWordSelected, GetNameDialog.Callback, Exporter.OnExportFinishedListener {
    override fun onFinished() {
        runOnUiThread {
            progress.dismiss()
            Toast.makeText(this, R.string.export_done, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onSelect(name: String) {
        progress = ProgressDialog.show(this, getString(R.string.loading_words), getString(R.string.please_wait))
        val exporter = Exporter(this, selectedWords, this)
        exporter.export(name)
    }

    fun onLoadCompleted(map: HashMap<Board, ArrayList<Translate>>) {
        this.selectedWords = map
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        customizeTabView()
        progress.dismiss()
    }

    override fun getWords(board: Board): ArrayList<Translate> {
        return selectedWords[board]!!
    }

    override fun getCategories(): List<Board> {
        return boards
    }

    override fun moveWord(fragment: ListFragment, word: Translate, board: Board, target: Board) {
        selectedWords[target]?.add(word)
        adapter.fragments[adapter.boards.indexOf(target)].updateList()
        updateBadge(board)
        updateBadge(target)
    }

    override fun onWordSelected(board: Board, word: Translate) {
        updateBadge(board)
    }

    private fun updateBadge(board: Board) {
        val pos = boards.indexOf(board)
        val size = selectedWords[board]?.filter { it.selected }?.size
        val tab = tabLayout.getTabAt(pos)
        val badge = tab?.customView?.findViewById<TextView>(R.id.textBadge)
        if (size!! > 0) {
            badge?.text = "$size"
            badge?.visibility = View.VISIBLE
        } else {
            badge?.visibility = View.GONE
        }
    }

    override fun getFragment(): ListFragment {
        val fragment = ListFragment()
        fragment.listener = this
        return fragment
    }

    lateinit var boards: ArrayList<Board>
    var selectedWords = HashMap<Board, ArrayList<Translate>>()

    lateinit var adapter: ExportPagerAdapter

    lateinit var progress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        title = getString(R.string.title_export)

        LoadCategoryTask(getAppDatabase()) { list -> initAdapter(list) }.execute()
        progress = ProgressDialog.show(this, getString(R.string.loading_words), getString(R.string.please_wait))
    }

    fun initAdapter(boards: ArrayList<Board>) {
        boards.add(OtherCategory())
        this.boards = boards
        adapter = ExportPagerAdapter(boards, supportFragmentManager, this)

        loadData()
        export.setOnClickListener { onButtonClicked() }

    }


    protected open fun onButtonClicked() {
        exportToFile()
    }

    protected open fun loadData() {

    }

    private fun exportToFile() {
        GetNameDialog(this, this).show()
    }

    private fun OtherCategory(): Board {
        return Board("Other", -1)
    }

    private fun customizeTabView() {
        (0 until tabLayout.tabCount)
                .map { tabLayout.getTabAt(it) }
                .forEach { it?.customView = getCustomView(it!!) }
    }

    @SuppressLint("InflateParams")
    private fun getCustomView(tab: TabLayout.Tab): View {
        val view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null, false)
        val text = view.findViewById<TextView>(R.id.textTitle)
        text.text = tab.text.toString().toUpperCase()
        val badge = view.findViewById<TextView>(R.id.textBadge)
        badge.visibility = View.GONE
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.select_diselect, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.select_all -> selectAll()
            R.id.diselect_all -> diselectAll()
            android.R.id.home -> finish()
            else -> {
                return false
            }
        }
        return true
    }

    private fun diselectAll() {
        selectedWords[boards[viewPager.currentItem]]?.forEach { it.selected = false }
        adapter.fragments[viewPager.currentItem].updateList()
        updateBadge(boards[viewPager.currentItem])
    }

    private fun selectAll() {
        selectedWords[boards[viewPager.currentItem]]?.forEach { it.selected = true }
        adapter.fragments[viewPager.currentItem].updateList()
        updateBadge(boards[viewPager.currentItem])
    }
}
