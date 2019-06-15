package ir.shahinsoft.notifictionary.model

/**
 * Created by shayan on 12/5/2017.
 */
class Quiz {
    var r1: Translate? = null
    var r2: Translate? = null
    var r3: Translate? = null
    var r4: Translate? = null
    var word: Translate? = null

    public fun isCurrect(index: Int): Boolean {
        return when (index) {
            1 -> word?.name.equals(r1?.name)
            2 -> word?.name.equals(r2?.name)
            3 -> word?.name.equals(r3?.name)
            4 -> word?.name.equals(r4?.name)
            else -> false
        }
    }
}