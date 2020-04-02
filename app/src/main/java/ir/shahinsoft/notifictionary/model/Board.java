package ir.shahinsoft.notifictionary.model;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by shayan4shayan on 4/5/18.
 */
@Entity
public class Board {
    @ColumnInfo(name = "name")
    private String name;
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "learned_count")
    private int learnedCount;

    @ColumnInfo(name = "total_count")
    private int totalCount;

    @ColumnInfo(name = "shared")
    private boolean shared = false;

    public Board(String name, int id) {

        this.name = name;
        this.id = id;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getLearnedCount() {
        return learnedCount;
    }

    public void setLearnedCount(int learnedCount) {
        this.learnedCount = learnedCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Board && ((Board) obj).id == id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
