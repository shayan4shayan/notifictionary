package ir.shahinsoft.notifictionary.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ir.shahinsoft.notifictionary.model.PhoneUsage;

@Dao
public interface PhoneUsageDao {
    @Insert
    void add(PhoneUsage usage);

    @Insert
    void addAll(List<PhoneUsage> usages);

    @Update
    void update(PhoneUsage usage);

    @Delete
    void delete(PhoneUsage usage);

    @Query("select * from phoneusage ;")
    List<PhoneUsage> selectAll();

}
