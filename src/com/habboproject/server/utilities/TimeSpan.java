package com.habboproject.server.utilities;

public class TimeSpan {
    private long difference;

    public static String millisecondsToDate(long time) {

        int SECOND = 1000;
        int MINUTE = 60 * SECOND;
        int HOUR = 60 * MINUTE;
        int DAY = 24 * HOUR;

        long ms = time;

        StringBuffer text = new StringBuffer("");
        if (ms > DAY) {
            text.append(ms / DAY).append(" days ");
            ms %= DAY;
        }
        if (ms > HOUR) {
            text.append(ms / HOUR).append(" hours ");
            ms %= HOUR;
        }
        if (ms > MINUTE) {
            text.append(ms / MINUTE).append(" minutes ");
            ms %= MINUTE;
        }
        if (ms > SECOND) {
            text.append(ms / SECOND).append(" seconds ");
            ms %= SECOND;
        }

        return text.toString();
    }

    public TimeSpan(long start, long finish) {
        this.difference = finish - start;
    }

    public long toSeconds() {
        return this.difference / 1000;
    }

    public long toMilliseconds() {
        return this.difference;
    }

    public long toMinutes() {
        return (this.difference / 1000) / 60;
    }

    public long toHours() {
        return ((this.difference / 1000) / 60) / 60;
    }

    public long toDays() {
        return (((this.difference / 1000) / 60) / 60) / 24;
    }

    public long toWeeks() {
        return ((((this.difference / 1000) / 60) / 60) / 24) / 7;
    }
}
