package ir.shahinsoft.notifictionary.activities

import android.os.Bundle
import android.util.Log
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.getAppDatabase
import ir.shahinsoft.notifictionary.model.Board
import ir.shahinsoft.notifictionary.model.Translate
import ir.shahinsoft.notifictionary.tasks.SaveSelectedTesk
import ir.shahinsoft.notifictionary.toast
import ir.shahinsoft.notifictionary.utils.Importer
import kotlinx.android.synthetic.main.activity_import.*

class ImportActivity : ExportActivity(), Importer.OnLoadCompleteListener, SaveSelectedTesk.OnFinishListener {
    override fun onSaveFinished() {
        toast(R.string.msg_words_saved)
        finish()
    }

    override fun onLoadComplete(map: HashMap<Board, ArrayList<Translate>>) {
        selectedWords = map
        boards.clear()
        boards.addAll(ArrayList(map.keys))
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        progress.dismiss()
        if (map.isEmpty()) {
            toast(R.string.error_read_file)
            finish()
        }
    }

    private val uri get() = intent.data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.title_import)
        export.setText(R.string.import_txt)
    }

    override fun onButtonClicked() {
        SaveSelectedTesk(selectedWords, getAppDatabase(), this).execute()
    }

    override fun loadData() {
        Log.d("ImportActivity","loading data from file")
        Importer(getStream()!!, getAppDatabase(), this).execute()
    }

    private fun getStream() = contentResolver.openInputStream(uri!!)
}
