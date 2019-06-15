package ir.shahinsoft.notifictionary.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.database.DatabaseWrapper
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.Translate
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Created by shayan4shayan on 2/20/18.
 */
class Importer(val stream: InputStream, val db: DatabaseWrapper, val listener: OnLoadCompleteListener) : AsyncTask<Uri, Int, HashMap<Category, ArrayList<Translate>>>() {
    override fun doInBackground(vararg params: Uri?): HashMap<Category, ArrayList<Translate>> {
        return parse()
    }


    companion object {
        const val RESULT_CODE = 10

        fun import_(context: Activity) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            val i = Intent.createChooser(intent, context.getString(R.string.select_back_up_file))
            context.startActivityForResult(i, RESULT_CODE)
        }
    }


    fun parse(): HashMap<Category, ArrayList<Translate>> {
        val str = read(stream)
        return try {
            val obj = JSONObject(str)
            parseV2(obj)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val arr = JSONArray(str)
                parseV1(arr)
            } catch (e: Exception) {
                e.printStackTrace()
                HashMap()
            }
        }

    }

    override fun onPostExecute(result: HashMap<Category, ArrayList<Translate>>) {
        listener.onLoadComplete(result)
    }

    private fun parseV1(arr: JSONArray): HashMap<Category, ArrayList<Translate>> {
        val map = HashMap<Category, ArrayList<Translate>>()
        map[Category("All", -1)] = parseV1JSON(arr)
        Log.d("Importer", map.toString())
        return map
    }

    private fun parseV2(obj: JSONObject): HashMap<Category, ArrayList<Translate>> {
        val map = HashMap<Category, ArrayList<Translate>>()
        val list = obj.getJSONArray("words")
        var startCatId = db.lastCategoryId()
        (0 until list.length()).map { list.getJSONObject(it) }
                .forEach { jsonObject ->

                    val cat = Category(jsonObject.getString("name"), ++startCatId)
                    val arr = ArrayList<Translate>()
                    map[cat] = arr
                    val jsonArray = jsonObject.getJSONArray("data")
                    (0 until jsonArray.length()).map { jsonArray.getJSONObject(it) }
                            .forEach {
                                arr.add(parseJSON(it))
                            }
                }

        return map
    }

    private fun parseV1JSON(arr: JSONArray): ArrayList<Translate> {
        val list = ArrayList<Translate>()

        (0 until arr.length())
                .map { arr.getJSONObject(it) }
                .map { parseJSON(it) }
                .forEach { list.add(it) }
        return list
    }

    private fun parseJSON(it: JSONObject?): Translate {
        val translate = Translate()
        translate.name = it?.getString("name")!!
        translate.translate = it.getString("translate")
        if (it.has("show_count")) translate.showCount = it.getInt("show_count")
        if (it.has("wrong_count")) translate.wrongCount = it.getInt("wrong_count")
        if (it.has("correct_count")) translate.correctCount = it.getInt("correct_count")
        return translate
    }

    fun onInsertToDB(list: java.util.ArrayList<Translate>) {
        list.forEach { db.insert(it) }
    }

    private fun read(stream: InputStream?): String? {
        val out = ByteArrayOutputStream()
        while (stream?.available()!! > 0) {
            out.write(stream.read())
        }
        return out.toString()
    }

    interface OnLoadCompleteListener {
        fun onLoadComplete(map: HashMap<Category, ArrayList<Translate>>)
    }
}