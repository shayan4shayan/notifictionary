package ir.shahinsoft.notifictionary.services.learning

import ir.shahinsoft.notifictionary.services.learning.database.Q
import ir.shahinsoft.notifictionary.services.learning.database.Records
import ir.shahinsoft.notifictionary.services.learning.database.States
import ir.shahinsoft.notifictionary.services.learning.models.ApproximateState
import ir.shahinsoft.notifictionary.services.learning.models.Record
import kotlin.random.Random

class Agent() {
    val qLearning = Q()
    val states = States()
    val records = Records()

    private val audacity = 0.8

    init {
        qLearning.load()
    }

    fun tekeAction(state: ApproximateState): Record.Action {
        states.states.append(state.id, state)
        val action = if (takeRandomAction()) {
            Record.Action.values().get(java.util.Random().nextInt(Record.Action.values().size))
        } else {
            qLearning.bestActionForState(state)
        }
        records.newRecord(Record(state).apply { this.action = action })
        return action
    }

    private fun takeRandomAction(): Boolean {
        val random = (0 until 100).random()
        return random > audacity * 100
    }

    fun updateRecord(sid: Int, action: Record.Action, userResponse: Boolean) {
        records.updateReward(states.states[sid], action, if (userResponse) 1 else -1)
        qLearning.update(states.states[sid], action, if (userResponse) 1 else -1)
    }

}