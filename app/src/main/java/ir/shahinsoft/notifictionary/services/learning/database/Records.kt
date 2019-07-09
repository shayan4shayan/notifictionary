package ir.shahinsoft.notifictionary.services.learning.database

import ir.shahinsoft.notifictionary.services.learning.models.ApproximateState
import ir.shahinsoft.notifictionary.services.learning.models.Record
import java.nio.charset.Charset

class Records {

    val records = ArrayList<Record>()

    fun newRecord(record: Record) {
        records.add(record)
    }

    fun updateReward(approximateState: ApproximateState?, action: Record.Action, i: Int) {
        records.find { it.state.id == approximateState?.id && it.action == action }?.reward = i
    }

    fun saveRecord(record: Record){
        PathProvider.instance.recordsPath.appendText(record.toJSON().toString(), Charset.forName("UTF-8"))
    }
}