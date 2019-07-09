package ir.shahinsoft.notifictionary.services.learning.database

import android.util.SparseArray
import ir.shahinsoft.notifictionary.services.learning.models.ApproximateState
import org.json.JSONArray
import java.io.FileInputStream
import java.io.FileOutputStream

class States {
    companion object {
        var instance: States? = null
            get() {
                if (field == null) field = States()
                return field
            }
    }

    val states = SparseArray<ApproximateState>()

    fun newState(state: ApproximateState) {
        states.put(state.id, state)
    }

    init {
        load()
    }

    fun save() {
        val text = getWritableString()
        val output = FileOutputStream(PathProvider.instance.statesPath)
        output.write(text.toByteArray())
        output.flush()
        output.close()
    }

    private fun getWritableString(): String {
        val array = JSONArray()
        (0 until states.size()).forEach {
            array.put(states.valueAt(it).toJSON())
        }
        return array.toString()
    }

    private fun load() {
        val input = FileInputStream(PathProvider.instance.statesPath)
        val str = read(input) ?: return
        val array = JSONArray(str)
        (0 until array.length()).forEach {
            val state = ApproximateState.parseJSON(array.getJSONObject(it))
            states.put(state.id, state)
        }
    }

    private fun read(input: FileInputStream): String? {
        val bufferSize = 1024 * 16
        var buffer = ByteArray(bufferSize)
        val builder = StringBuilder()
        if (input.available() == 0) return null;
        while (input.available() >= bufferSize) {
            input.read(buffer)
            builder.append(String(buffer))
            buffer = ByteArray(bufferSize)
        }
        buffer = ByteArray(input.available())
        input.read(buffer)
        builder.append(String(buffer))
        return builder.toString()
    }
}
