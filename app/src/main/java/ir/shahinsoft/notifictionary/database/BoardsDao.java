package ir.shahinsoft.notifictionary.database;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import ir.shahinsoft.notifictionary.model.Board;

@Dao
public interface BoardsDao {
    @Insert
    void insert(Board board);

    @Insert
    void insert(Board... categories);

    @Insert
    void insert(List<Board> categories);

    @Delete
    void delete(Board board);

    @Update
    void update(Board board);

    @Query("select * from Board ;")
    List<Board> getCategories();

    @Query("select id from Board order by id desc;")
    int[] lastId();
}
