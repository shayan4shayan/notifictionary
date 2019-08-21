package ir.shahinsoft.notifictionary.services.learning.database

import android.annotation.SuppressLint
import android.util.Log
import android.util.SparseArray
import ir.shahinsoft.notifictionary.services.learning.models.ApproximateState
import ir.shahinsoft.notifictionary.services.learning.models.Record
import java.io.*
import java.lang.IndexOutOfBoundsException
import kotlin.concurrent.thread
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

    fun allRewardsByState(state: ApproximateState): Map<Record.Action, Double> {
        return (0 until data.size()).map { data.keyAt(it) }.filter { it / 10 == state.id }
                .map { Pair<Record.Action, Double>(Record.Action.values()[it % 10], data.get(it)) }.toMap()
    }

    fun update(state: ApproximateState, action: Record.Action, reward: Int) {
        val newQ = (1 - learningRate) * valueOf(state, action) + learningRate * (reward + discountFactor * bestRewardOf(action))
        data.put(keyOf(state, action), newQ)
        //thread { save(data.clone()) }
    }

    private fun bestRewardOf(action: Record.Action): Double {
        return (0 until data.size()).map { data.keyAt(it) }.filter { it % 10 == action.ordinal }.map { data[it] }.max()
                ?: defultValue
    }

    fun save() {
        val out = FileOutputStream(PathProvider.instance.QPath)
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
        Log.i("LearningService", "stateId: " + state.id)
        try {
            val map = allRewardsByState(state)
            val maxkey = map.maxBy { it.value }?.key
                    ?: return Record.Action.values().random()

            return if (map[maxkey] ?: error("") < defultValue) {
                // all actions have reward bellow the default value so the biggest is maxkey
                if (map.size == Record.Action.values().size) {
                    maxkey
                }
                // there is at least one action that has value equal to default value but not selected till now and does not exists in list
                else {
                    Record.Action.values().filter { map[it] == null }.random()
                }
            } else {
                maxkey
            }
        } catch (e: IndexOutOfBoundsException) {
            return Record.Action.values().random()
        }
    }

}