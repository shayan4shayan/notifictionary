package ir.shahinsoft.notifictionary.database;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ir.shahinsoft.notifictionary.model.Category;

@Dao
public interface CategoriesDao {
    @Insert
    void insert(Category category);

    @Insert
    void insert(Category... categories);

    @Insert
    void insert(List<Category> categories);

    @Delete
    void delete(Category category);

    @Update
    void update(Category category);

    @Query("select * from category ;")
    List<Category> getCategories();

    @Query("select id from category order by id desc;")
    int[] lastId();
}
