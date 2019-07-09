package ir.shahinsoft.notifictionary.services.learning.database

import android.annotation.SuppressLint
import android.util.SparseArray
import ir.shahinsoft.notifictionary.services.learning.models.ApproximateState
import ir.shahinsoft.notifictionary.services.learning.models.Record
import java.io.*
import java.lang.IndexOutOfBoundsException
import kotlin.contracts.Returns

class Q {
    var instance: Q? = null
        get() {
            if (field == null) field = Q()
            return field
        }

    val learningRate = 0.75

    val discountFactor = 0.25

    val defultValue = 1.toDouble()

    @SuppressLint("UseSparseArrays")
    private val data = SparseArray<Double>()

    fun valueOf(state: ApproximateState, action: Record.Action): Double {

        val index = keyOf(state, action)

        if (data[index] == null) data.put(index, defultValue)

        return data[index]
    }

    private fun keyOf(state: ApproximateState, action: Record.Action): Int {
        return state.id * 10 + action.ordinal
    }

    fun update(state: ApproximateState, action: Record.Action, reward: Int) {
        val newQ = (1 - learningRate) * valueOf(state, action) + learningRate * (reward + discountFactor * bestRewardOf(action))
        data.put(keyOf(state, action), newQ)
    }

    private fun bestRewardOf(action: Record.Action): Double {
        return (0 until data.size()).map { data.keyAt(it) }.filter { it % 10 == action.ordinal }.map { data[it] }.max()
                ?: defultValue
    }

    fun save() {
        val out = FileOutputStream(PathProvider.instance.q)
        (0 until data.size()).map { "${data.keyAt(it)} ${data.valueAt(it)}" }.forEach {
            out.write(it.toByteArray())
            out.write("\n".toByteArray())
        }
        out.close()
    }

    fun load() {
        val input = InputStreamReader(BufferedInputStream(FileInputStream(PathProvider.instance.QPath)))
        input.readLines().forEach {
            val keyvalue = it.split(' ')
            val key = keyvalue[0].toInt()
            val value = keyvalue[1].toDouble()
            data.put(key, value)
        }
    }

    fun bestActionForState(state: ApproximateState): Record.Action {
        try {
            val sint = state.id
            val foundKeys = (0 until data.size()).map { data.keyAt(it) }.filter { it / 10 == sint }
            val maxkey = foundKeys.maxBy { data.get(it) }
                    ?: return Record.Action.values().get(java.util.Random().nextInt(Record.Action.values().size))

            return if (data[maxkey] < defultValue) {
                if (foundKeys.size == Record.Action.values().size) {
                    Record.Action.values().get(maxkey % 10)
                } else {
                    val index = Record.Action.values().map { Record.Action.values().indexOf(it) }.filter { action -> !foundKeys.map { key -> key % 10 }.contains(action) }.random()
                    Record.Action.values().get(index)
                }
            } else {
                Record.Action.values()[maxkey % 10]
            }
        } catch (e: IndexOutOfBoundsException) {
            return Record.Action.values().random()
        }
    }

}