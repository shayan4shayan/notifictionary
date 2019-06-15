package ir.shahinsoft.notifictionary.model;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by shayan4shayan on 4/5/18.
 */
@Entity
public class Category {
    @ColumnInfo(name = "name")
    private String name;
    @PrimaryKey
    private int id;

    private boolean isSelected;

    @ColorInt
    @ColumnInfo(name = "color")
    private int color = Color.WHITE;

    public Category(String name, int id) {

        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Category && ((Category) obj).id == id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
