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

    private boolean isSelected;

    public Board(String name, int id) {

        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getLearnedCount() {
        return learnedCount;
    }

    public void setLearnedCount(int learnedCount) {
        this.learnedCount = learnedCount;
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
