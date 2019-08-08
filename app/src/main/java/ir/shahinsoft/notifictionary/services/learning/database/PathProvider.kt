package ir.shahinsoft.notifictionary.services.learning.database

import android.content.Context
import java.io.File

class PathProvider {


    companion object {
        val instance = PathProvider()
    }

    lateinit var statesPath: File
    lateinit var recordsPath: File
    lateinit var QPath: File

    private val learningDirectory = "learning"
    private val states = "states.txt"
    private val records = "records.txt"
    private val q = "Q.txt"

    fun init(context: Context) {
        statesPath = File("${context.cacheDir}/$learningDirectory/$states")
        recordsPath = File("${context.cacheDir}/$learningDirectory/$records")
        QPath = File("${context.cacheDir}/$learningDirectory/$q")

        val directory = File("${context.cacheDir}/$learningDirectory")
        if (!directory.exists()) directory.mkdirs()

        statesPath.apply { if (!exists()) createNewFile() }
        recordsPath.apply { if (!exists()) createNewFile() }
        QPath.apply { if (!exists()) createNewFile() }
    }
}