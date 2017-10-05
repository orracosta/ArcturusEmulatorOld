package com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard;

import com.habboproject.server.utilities.JsonData;

/**
 * Created by brend on 03/02/2017.
 */
public class ResetItemData implements JsonData {
    private int lastDay;
    private int lastDayOfYear;
    private int lastMonth;
    private int lastDayOfWeek;
    private int lastWeekOfWeekyear;

    public ResetItemData(int lastDay, int lastDayOfYear, int lastMonth, int lastDayOfWeek, int lastWeekOfWeekyear) {
        this.lastDay = lastDay;
        this.lastDayOfYear = lastDayOfYear;
        this.lastMonth = lastMonth;
        this.lastDayOfWeek = lastDayOfWeek;
        this.lastWeekOfWeekyear = lastWeekOfWeekyear;
    }

    public int getLastDay() {
        return this.lastDay;
    }

    public void setLastDay(int lastDay) {
        this.lastDay = lastDay;
    }

    public int getLastMonth() {
        return this.lastMonth;
    }

    public void setLastMonth(int lastMonth) {
        this.lastMonth = lastMonth;
    }

    public int getLastWeekOfWeekyear() {
        return this.lastWeekOfWeekyear;
    }

    public void setLastWeekOfWeekyear(int lastWeekOfWeekyear) {
        this.lastWeekOfWeekyear = lastWeekOfWeekyear;
    }

    public int getLastDayOfYear() {
        return this.lastDayOfYear;
    }

    public void setLastDayOfYear(int lastDayOfYear) {
        this.lastDayOfYear = lastDayOfYear;
    }
}
