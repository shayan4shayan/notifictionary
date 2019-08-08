package ir.shahinsoft.notifictionary.services.learning

import android.os.AsyncTask

class UpdateDataTask(val agent: Agent) : AsyncTask<Unit,Unit,Unit>(){

    override fun doInBackground(vararg p0: Unit?) {
        agent.qLearning.save()
        agent.states.save()
    }

}