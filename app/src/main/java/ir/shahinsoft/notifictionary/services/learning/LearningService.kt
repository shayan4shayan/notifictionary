package ir.shahinsoft.notifictionary.services.learning

import ir.shahinsoft.notifictionary.services.learning.models.ApproximateState
import ir.shahinsoft.notifictionary.services.learning.models.FullState
import ir.shahinsoft.notifictionary.services.learning.models.Record
import java.util.*


class LearningService {

    companion object {
        private var instance: LearningService? = null
        fun getInstance(): LearningService {
            if (instance == null) instance = LearningService()
            return instance!!
        }
    }

    val agent = Agent()

    fun tellState(isScreenOn: Boolean): FullState {
        val currentTime = Calendar.getInstance().time
        val day = getDayOfWeek()
        return FullState(date = currentTime, day = FullState.WeekDay.values()[day], isScreenOn = isScreenOn)
    }

    private fun getDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getNextNotificationTime(isScreenOn : Boolean) : Pair<Pair<ApproximateState,Record.Action>,Long>{

        val pair = takeAction(isScreenOn)

        val action = pair.second

        val time = when(action){
            Record.Action.NOW -> (60000 until 5*60000).random().toLong()
            Record.Action.DELAY_ONE_HOUR -> (1000*60*30 until 1000*60*60).random().toLong()
            Record.Action.DELAY_RANDOM_AFTER_ONE_HOUR -> (1000 * 60 * 60 until 1000*60*60*2).random().toLong()
            else -> 0
        }

        return Pair(pair,time)

    }

    fun takeAction(isScreenOn: Boolean): Pair<ApproximateState,Record.Action> {
        val state = tellState(isScreenOn).approximate()
        agent.states.newState(state)
        return Pair(state,agent.tekeAction(state))
    }

    fun reward(sid: Int, action: Record.Action, userResponse: Boolean) {
        agent.updateRecord(sid, action, userResponse)
    }

}