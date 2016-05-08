package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 16-10-2015 21:00.
 */
public class CrackableReward
{
    public final int itemId;
    public final int count;
    public final int[] prizes;
    public final String achievementTick;
    public final String achievementCracked;

    public CrackableReward(ResultSet set) throws SQLException
    {
        this.itemId = set.getInt("item_id");
        this.count = set.getInt("count");
        this.achievementTick = set.getString("achievement_tick");
        this.achievementCracked = set.getString("achievement_cracked");

        String[] data = set.getString("prizes").split(";");
        this.prizes = new int[data.length];

        for(int i = 0; i < this.prizes.length; i++)
        {
            try
            {
                this.prizes[i] = Integer.valueOf(data[i]);
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    public int getRandomReward()
    {
        return this.prizes[Emulator.getRandom().nextInt(this.prizes.length)];
    }
}
