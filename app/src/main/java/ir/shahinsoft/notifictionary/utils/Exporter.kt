package ir.shahinsoft.notifictionary.utils

import android.content.Context
import android.os.Environment
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.lang.Thread
import java.util.ArrayList

/**
 * Created by shayan4shayan on 2/20/18.
 */
class Exporter(val context: Context, private val toExport: HashMap<Category, ArrayList<Translate>>, val onExportFinishedListener: OnExportFinishedListener) : Runnable {

    var path = ""

    override fun run() {
        val str = getJSONString(toExport)
        write(str)
        onExportFinishedListener.onFinished()
    }

    private fun write(str: String) {
        val extPath = Environment.getExternalStorageDirectory()
        val appPath = extPath.absolutePath + "/notifictionary"
        val mainFile = File(appPath)
        if (!mainFile.exists()) mainFile.mkdir()
        path = "$appPath/$path.nfb"
        val file = File(path)
        val fos = FileOutputStream(file)
        fos.write(str.toByteArray())
        fos.flush()
    }

    private fun getJSONString(words: HashMap<Category, ArrayList<Translate>>): String {
        val json = JSONObject()
        json.put("version","2")
        val str = JSONArray()
        words.keys.forEach { str.put(getJsonObject(it, words[it])) }
        json.put("words",str)
        return json.toString()
    }

    private fun getJsonObject(it: Category, arrayList: ArrayList<Translate>?): JSONObject {
        val obj = JSONObject()
        obj.put("name", it.name)
        obj.put("color",it.color)
        val arr = JSONArray()
        arrayList?.forEach { if (it.selected) arr.put(getWordJSONObject(it)) }
        obj.put("data", arr)
        return obj
    }


    private fun getWordJSONObject(it: Translate): JSONObject {
        val obj = JSONObject()
        obj.put("name", it.name)
        obj.put("translate", it.translate)
        obj.put("correct_count", it.correctCount)
        obj.put("show_count", it.showCount)
        obj.put("wrong_count", it.wrongCount)
        return obj
    }

    fun export(str: String) {
        this.path = str
        Thread(this).start()
    }

    interface OnExportFinishedListener {
        fun onFinished()
    }
}