package ir.shahinsoft.notifictionary.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ir.shahinsoft.notifictionary.model.Translate;

@Dao
public interface WordsDao {
    @Insert
    void insert(Translate translate);

    @Insert
    void insertAll(Translate... translates);

    @Insert
    void insertAll(List<Translate> translates);

    @Query("select * from translate;")
    List<Translate> getTranslateList();

    @Update
    void update(Translate translate);

    @Delete
    void delete(Translate translate);

    @Delete
    void delete(List<Translate> translates);

    @Query("delete from translate where category_id = :catId")
    void deleteByCategoryId(int catId);

    @Query("select * from translate order by id desc limit :start , :end;")
    List<Translate> getTranslates(int start, int end);

    @Query("select * from translate where category_id = :catId order by id limit :start ,:end;")
    List<Translate> getTranslates(int catId, int start, int end);

    @Query("select count(id) as size from translate")
    int getSize();

    @Query("select id from translate order by id desc ;")
    int[] lastId();

    @Query("select * from translate where category_id = :catId order by id desc ;")
    List<Translate> getTranslates(int catId);

    @Query("select * from translate where id = :id ;")
    Translate select(int id);
}
