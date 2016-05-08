package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 4-8-2015 11:20.
 */
public class WordFilterWord
{
    public final String key;
    public final String replacement;
    public final boolean hideMessage;
    public final boolean autoReport;

    public WordFilterWord(ResultSet set) throws SQLException
    {
        this.key = set.getString("key");
        this.replacement = set.getString("replacement");
        this.hideMessage = set.getInt("hide") == 1;
        this.autoReport = set.getInt("report") == 1;
    }

    public WordFilterWord(String key, String replacement)
    {
        this.key = key;
        this.replacement = replacement;
        this.hideMessage = false;
        this.autoReport = false;
    }
}
