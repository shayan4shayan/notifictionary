package ir.shahinsoft.notifictionary.quizprovider

import ir.shahinsoft.notifictionary.model.Translate

/**
 * Created by shayan on 12/4/2017.
 */
class Cosine {

    fun getSimilarity(s1: Translate, s2: Translate): Float {

        val map = BuildWordsMap(s1, s2)

        val arr1 = IntArray(size = map.size)
        val arr2 = IntArray(size = map.size)

        for (char in s1.name) {
            arr1[map[char]!!]++
        }
        for (char in s2.name) {
            arr2[map[char]!!]++
        }
        return cosine(arr1, arr2)

    }

    private fun BuildWordsMap(s1: Translate, s2: Translate): HashMap<Char, Int> {
        val map = HashMap<Char, Int>()
        var index = 0
        for (char in s1.name) {
            if (!map.containsKey(char)) {
                map[char] = index
                index++
            }
        }
        for (char in s2.name) {
            if (!map.containsKey(char)) {
                map[char] = index
                index++
            }
        }
        return map
    }

    private fun cosine(i1: IntArray, i2: IntArray): Float = ((multiply(i1, i2) / (sizeOf(i1) * sizeOf(i2))))

    private fun sizeOf(i1: IntArray): Float {
        var sum = 0
        (0 until i1.size).forEach { sum += i1[it] * i1[it] }
        return Math.sqrt(sum.toDouble()).toFloat()
    }

    private fun multiply(i1: IntArray, i2: IntArray): Int = (0 until i1.size).sumBy { i1[it] * i2[it] }

}