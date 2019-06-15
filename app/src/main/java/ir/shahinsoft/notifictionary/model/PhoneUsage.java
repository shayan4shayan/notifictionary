package ir.shahinsoft.notifictionary.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class PhoneUsage {

    @PrimaryKey
    int id = 0;

    @ColumnInfo(name = "start_time_mils")
    String startTime = "";

    @ColumnInfo(name = "end_time_mils")
    String endTime = "";

    @ColumnInfo(name = "score")
    int score = 0;

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public long getStartTimeInt() {
        if (startTime.isEmpty()) return 0;
        String[] vals = startTime.split(":");
        int hour = Integer.parseInt(vals[0]);
        int min = Integer.parseInt(vals[1]);
        int sec = Integer.parseInt(vals[2]);
        return hour * (1000 * 60 * 60) + min * (1000 * 60) + sec * 1000;
    }

    public long getEndTimeInt() {
        if (endTime.isEmpty()) return 0;
        String[] vals = endTime.split(":");
        int hour = Integer.parseInt(vals[0]);
        int min = Integer.parseInt(vals[1]);
        int sec = Integer.parseInt(vals[2]);
        return hour * (1000 * 60 * 60) + min * (1000 * 60) + sec * 1000;
    }


    public boolean isValid() {
        long start = getStartTimeInt();
        long end = getEndTimeInt();
        return end > start && end - start > 5 * 60 * 1000;
    }


    private String getFormattedTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return format.format(date);
    }


    /**
     * returns 1 if time is in range and 0 for not in rage and -1 if the range is unknown
     */
    public int isTimeInRange(long time) {
        if (startTime.isEmpty() || endTime.isEmpty()) return -1;
        if (time > getStartTimeInt() && time < getEndTimeInt()) return 1;
        else return 0;
    }

    public boolean hasCollision(PhoneUsage usage) {
        boolean isValid;
        Log.d("hello", "1");
        isValid = isInside(usage) || usage.isInside(this);
        Log.d("hello", "2");
        isValid = isValid || hasValidCollisionFromSide(usage) ||
                usage.hasValidCollisionFromSide(this);
        Log.d("hello", "3");
        return isValid;
    }

    public void merge(PhoneUsage usage) {
        long start = getStartTimeInt();
        long end = getEndTimeInt();
        if (usage.getStartTimeInt() < start) setStartTime(usage.getStartTime());
        if (usage.getEndTimeInt() > end) setEndTime(usage.getEndTime());
    }

    public boolean hasValidCollisionFromSide(PhoneUsage usage) {
        long start = getStartTimeInt();
        long end = getEndTimeInt();
        return start > usage.getStartTimeInt() && (usage.getEndTimeInt() - end) > 5 * 60 * 1000;
    }

    public boolean isInside(PhoneUsage usage) {
        long start = getStartTimeInt();
        long end = getEndTimeInt();
        return start < usage.getStartTimeInt() && end > usage.getEndTimeInt();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US, "\nscore:%d\nstartTime:%s,%d\nendTime:%s,%d\n", score, startTime, getStartTimeInt(), endTime, getEndTimeInt());
    }

    public boolean isInside(int i) {
        return getStartTimeInt() >= i && getEndTimeInt() <= i;
    }
}