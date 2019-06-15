package ir.shahinsoft.notifictionary.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by shayan on 9/28/2017.
 */

@Entity
public class Translate implements Serializable, Parcelable {

    @PrimaryKey
    private int id;
    @ColumnInfo(name = "word")
    private String name;
    @ColumnInfo(name = "translate")
    private String translate;
    @ColumnInfo(name = "show_count")
    private int showCount;
    @ColumnInfo(name = "wrong_count")
    private int wrongCount;
    @ColumnInfo(name = "correct_count")
    private int correctCount;

    public boolean selected;
    @ColumnInfo(name = "category_id")
    private int catId = 0;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "lang")
    private String lang;

    protected Translate(Parcel in) {
        id = in.readInt();
        name = in.readString();
        translate = in.readString();
        showCount = in.readInt();
        wrongCount = in.readInt();
        correctCount = in.readInt();
        selected = in.readByte() != 0;
    }

    public Translate() {

    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static final Creator<Translate> CREATOR = new Creator<Translate>() {
        @Override
        public Translate createFromParcel(Parcel in) {
            return new Translate(in);
        }

        @Override
        public Translate[] newArray(int size) {
            return new Translate[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getName() {
        return name;
    }

    public String getTranslate() {
        return translate;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public int getShowCount() {
        return showCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public void incrementCorrect() {
        correctCount++;
    }

    public void incrementWrong() {
        wrongCount++;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(translate);
        dest.writeInt(showCount);
        dest.writeInt(wrongCount);
        dest.writeInt(correctCount);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getCatId() {
        return catId;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }
}
