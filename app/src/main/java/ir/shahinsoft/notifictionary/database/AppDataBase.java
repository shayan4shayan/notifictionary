package ir.shahinsoft.notifictionary.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ir.shahinsoft.notifictionary.model.Board;
import ir.shahinsoft.notifictionary.model.History;
import ir.shahinsoft.notifictionary.model.PhoneUsage;
import ir.shahinsoft.notifictionary.model.Translate;

@Database(entities = {Translate.class, Board.class, History.class, PhoneUsage.class}, version = 4, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public abstract WordsDao wordsDao();

    public abstract BoardsDao categoriesDao();

    public abstract HistoryDao historyDao();

    public abstract PhoneUsageDao phoneUsageDao();

}
