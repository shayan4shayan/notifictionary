package ir.shahinsoft.notifictionary.services.learning

class LearningService() {

    companion object {
        private var instance: LearningService? = null
        public fun getInstance(): LearningService {
            if (instance == null) instance = LearningService()
            return instance!!
        }
    }

    val nextNotificationTime: Long
        get() {
            return 20 * 60 * 1000
        }


}