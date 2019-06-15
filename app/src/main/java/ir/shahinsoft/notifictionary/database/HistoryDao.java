package ir.shahinsoft.notifictionary.database;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import ir.shahinsoft.notifictionary.model.History;

@Dao
public interface HistoryDao {
    @Delete
    void delete(History history);

    @Insert
    void insert(History history);

    @Query("select * from history ;")
    List<History> getHistories();

    @Query("select id from history order by id desc")
    int[] lastId();

    @Query("delete from history where 1;")
    void deleteAll();
}
