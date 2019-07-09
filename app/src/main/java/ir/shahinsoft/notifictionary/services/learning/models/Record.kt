package ir.shahinsoft.notifictionary.services.learning.models

import org.json.JSONObject

data class Record(val state: ApproximateState) {

    companion object {
        fun parseJSON(json: JSONObject): Record {
            return Record(ApproximateState.parseJSON(json.getJSONObject("state"))).apply {
                val action = json.getInt("action")
                if (action >= 0) this.action = Action.values()[action]
                val reward = json.getString("reward")
                if (reward != "null") this.reward = reward.toInt()
            }
        }
    }

    enum class Action { NOW, DELAY_ONE_HOUR, DELAY_RANDOM_AFTER_ONE_HOUR, DO_NOT_SEND }

    var action: Action? = null

    val isActionSelected get() = action != null

    var reward: Int? = null

    val isRewardReceived get() = reward != null

    fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("action", action ?: -1)
            put("state", state.toJSON())
            put("reward", reward ?: "null")
        }
    }

}