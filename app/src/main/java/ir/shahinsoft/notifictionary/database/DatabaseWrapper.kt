package ir.shahinsoft.notifictionary.database

import android.content.Context
import androidx.room.Room
import ir.shahinsoft.notifictionary.model.Category
import ir.shahinsoft.notifictionary.model.History
import ir.shahinsoft.notifictionary.model.PhoneUsage
import ir.shahinsoft.notifictionary.model.Translate

class DatabaseWrapper(context: Context) {
    companion object {
        var instance: DatabaseWrapper? = null
        fun getInstance(context: Context): DatabaseWrapper {
            if (instance != null) return instance!!
            instance = DatabaseWrapper(context)
            return instance!!
        }

        const val perpage = 50
    }

    val database: AppDataBase = Room.databaseBuilder(context, AppDataBase::class.java, "notifictionary").fallbackToDestructiveMigration().build()

    fun insert(translate: Translate) = database.wordsDao().insert(translate)

    fun insert(translates: List<Translate>) = database.wordsDao().insertAll(translates)

    fun delete(translate: Translate) = database.wordsDao().delete(translate)

    fun update(translate: Translate) = database.wordsDao().update(translate)

    fun selectAllTranslates() = ArrayList(database.wordsDao().translateList)

    fun selectTranslates(page: Int) = ArrayList(database.wordsDao().getTranslates(page * perpage, (page + 1) * perpage))

    fun selectTranslates(catId: Int, page: Int) = ArrayList(database.wordsDao().getTranslates(catId, page * perpage, (page + 1) * perpage))

    fun insert(category: Category) = database.categoriesDao().insert(category)

    fun delete(category: Category) = database.categoriesDao().delete(category)

    fun update(category: Category) = database.categoriesDao().update(category)

    fun selectCategories() = ArrayList(database.categoriesDao().categories)

    fun insert(params: Array<out Translate>) = database.wordsDao().insertAll(params.toList())

    fun lastTranslateId(): Int {
        val ints = database.wordsDao().lastId()
        return if (ints.isNotEmpty()) {
            ints[0]
        } else 1
    }

    fun insert(params: Array<out Category?>) = database.categoriesDao().insert(params.toList())

    fun lastCategoryId(): Int {
        val ids = database.categoriesDao().lastId()
        return if (ids.isEmpty()) 1 else ids[0]
    }

    fun selectAllTranslates(id: Int) = ArrayList(database.wordsDao().getTranslates(id))

    fun insert(history: History) = database.historyDao().insert(history)

    fun delete(history: History) = database.historyDao().delete(history)

    fun getHistories() = ArrayList(database.historyDao().histories)

    fun deleteWordsByCategoryId(id: Int) = database.wordsDao().deleteByCategoryId(id)

    fun lastHistoryId(): Int {
        val ids = database.historyDao().lastId()
        return if (ids.isEmpty()) 1 else ids[0]

    }

    fun translateIds(): IntArray = database.wordsDao().lastId()
    fun select(id: Int): Translate = database.wordsDao().select(id)
    fun deleteHistories() = database.historyDao().deleteAll()
    fun translateIdsForReminder(goal: Int) = database.wordsDao().translateList.filter { it.correctCount < goal }.map { it.id }

    fun getPhoneUsageId(): Int {
        val all = database.phoneUsageDao().selectAll()
        return if (all == null || all.isEmpty()) 1
        else all.last().id + 1
    }

    fun selectAllPhoneUsages() = ArrayList<PhoneUsage>(database.phoneUsageDao().selectAll())

    fun addPhoneUsage(usage: PhoneUsage) = database.phoneUsageDao().add(usage)

    fun editPhoneUsage(usage: PhoneUsage) = database.phoneUsageDao().update(usage)

    fun removePhoneUsage(usage: PhoneUsage) = database.phoneUsageDao().delete(usage)

    fun addAllPhoneUsages(usages: ArrayList<PhoneUsage>) = database.phoneUsageDao().addAll(usages)

}